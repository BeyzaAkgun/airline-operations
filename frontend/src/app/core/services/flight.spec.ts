import { TestBed } from '@angular/core/testing';
import { FlightService } from './flight';
import type { Flight } from '../models/flight.model';
import { provideHttpClient } from '@angular/common/http';
import {
  provideHttpClientTesting,
  HttpTestingController
} from '@angular/common/http/testing';
import { HttpErrorResponse } from '@angular/common/http';

describe('FlightService', () => {
  let service: FlightService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(FlightService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load flights and publish them to subscribers', () => {
    const flights: Flight[] = [
      {
        flightNumber: 'TEST01',
        origin: 'Istanbul',
        destination: 'London',
        status: 'SCHEDULED',
        departureTime: '2026-09-25T12:00:00Z',
        gate: 'A01'
      }
    ];

    let publishedFlights: readonly Flight[] = [];

    const subscription = service.flights$.subscribe(value => {
      publishedFlights = value;
    });

    service.loadFlights().subscribe();

    const request = httpTesting.expectOne(
      'http://localhost:8080/api/flights'
    );

    expect(request.request.method).toBe('GET');
    expect(publishedFlights).toEqual([]);

    request.flush(flights);

    expect(publishedFlights).toEqual(flights);
    expect(service.getFlights()).toEqual(flights);

    subscription.unsubscribe();
  });

it('should add a new flight without changing the previous array', () => {
  const previousFlights = service.getFlights();

  const newFlight: Flight = {
    flightNumber: 'TEST01',
    origin: 'Istanbul',
    destination: 'London',
    status: 'SCHEDULED',
    departureTime: '2026-09-25T12:00:00Z',
    gate: 'A01'
  };

  service.addFlight(newFlight).subscribe();

  const request = httpTesting.expectOne(
    'http://localhost:8080/api/flights'
  );

  expect(request.request.method).toBe('POST');
  expect(request.request.body).toEqual(newFlight);

  
  expect(service.getFlights()).toEqual([]);

  request.flush(newFlight);


  expect(service.getFlights()).toEqual([newFlight]);

  
  expect(previousFlights).toEqual([]);
  expect(service.getFlights()).not.toBe(previousFlights);
});

  it('should keep the list unchanged when adding a flight returns 409', () => {
  const previousFlights = service.getFlights();

  const duplicateFlight: Flight = {
    flightNumber: 'C4K6',
    origin: 'Istanbul',
    destination: 'London',
    status: 'SCHEDULED',
    departureTime: '2026-09-25T12:00:00Z',
    gate: 'A01'
  };

  let receivedError: unknown;

  service.addFlight(duplicateFlight).subscribe({
    next: () => {
      throw new Error('Expected the request to fail.');
    },
    error: (err: unknown) => {
      receivedError = err;
    }
  });

  const request = httpTesting.expectOne(
    'http://localhost:8080/api/flights'
  );

  expect(request.request.method).toBe('POST');

  request.flush(
    {
      timestamp: '2026-09-25T12:00:00Z',
      status: 409,
      message: 'Flight C4K6 already exists.',
      path: '/api/flights',
      fieldErrors: {}
    },
    {
      status: 409,
      statusText: 'Conflict'
    }
  );

  expect(receivedError).toBeInstanceOf(HttpErrorResponse);

  if (!(receivedError instanceof HttpErrorResponse)) {
    throw new Error('Expected an HTTP error.');
  }

  expect(receivedError.status).toBe(409);
  expect(service.getFlights()).toBe(previousFlights);
  expect(service.getFlights()).toEqual([]);
});
  
  it('should update a flight without changing the previous data', () => {
  const flight: Flight = {
    flightNumber: 'C4K6',
    origin: 'Rome',
    destination: 'Los Angeles',
    status: 'SCHEDULED',
    departureTime: '2026-09-25T14:00:00Z',
    gate: ''
  };

  // Başlangıç listesini yükle.
  service.loadFlights().subscribe();

  httpTesting.expectOne({
    method: 'GET',
    url: 'http://localhost:8080/api/flights'
  }).flush([flight]);

  const previousFlights = service.getFlights();

  const updatedFlight: Flight = {
    ...flight,
    status: 'DELAYED',
    gate: 'B10'
  };

  service.updateFlight(updatedFlight).subscribe();

  const request = httpTesting.expectOne({
    method: 'PUT',
    url: 'http://localhost:8080/api/flights/C4K6'
  });

  // Gövdede flightNumber bulunmamalı.
  expect(request.request.body).toEqual({
    origin: updatedFlight.origin,
    destination: updatedFlight.destination,
    status: updatedFlight.status,
    departureTime: updatedFlight.departureTime,
    gate: updatedFlight.gate
  });

  // Cevap gelmeden liste değişmemeli.
  expect(service.getFlightByNumber('C4K6')).toEqual(flight);

  request.flush(updatedFlight);

  expect(service.getFlights().length).toBe(previousFlights.length);
  expect(service.getFlightByNumber('C4K6')).toEqual(updatedFlight);
  expect(service.getFlights()).not.toBe(previousFlights);

  // Eski dizi ve uçuş nesnesi korunmalı.
  expect(previousFlights).toEqual([flight]);
  expect(flight.status).toBe('SCHEDULED');
  expect(flight.gate).toBe('');
});

it('should delete a flight without changing the previous array', () => {
  const flight: Flight = {
    flightNumber: 'D8K2',
    origin: 'Madrid',
    destination: 'Paris',
    status: 'CANCELED',
    departureTime: '2026-09-25T07:15:00Z',
    gate: 'C08'
  };

  service.loadFlights().subscribe();

  httpTesting.expectOne({
    method: 'GET',
    url: 'http://localhost:8080/api/flights'
  }).flush([flight]);

  const previousFlights = service.getFlights();

  service.deleteFlight('D8K2').subscribe();

  const request = httpTesting.expectOne({
    method: 'DELETE',
    url: 'http://localhost:8080/api/flights/D8K2'
  });

  // Backend cevap vermeden uçuş listede kalmalı.
  expect(service.getFlightByNumber('D8K2')).toEqual(flight);

  request.flush(null, {
    status: 204,
    statusText: 'No Content'
  });

  expect(service.getFlights()).toEqual([]);
  expect(service.getFlightByNumber('D8K2')).toBeNull();

  // Önceki dizi korunmalı.
  expect(previousFlights).toEqual([flight]);
  expect(service.getFlights()).not.toBe(previousFlights);
});

});



  