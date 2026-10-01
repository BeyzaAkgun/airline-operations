import { Injectable,inject } from '@angular/core';
import type { Flight, FlightStatus } from '../models/flight.model';
import { BehaviorSubject,Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class FlightService {
  private readonly http=inject(HttpClient);
  private readonly apiUrl="http://localhost:8080/api/flights"
  private readonly flightsSubject=new BehaviorSubject<readonly Flight[]>([]);
  readonly flights$=this.flightsSubject.asObservable()
  getFlights():readonly Flight[]{
    return this.flightsSubject.getValue();
  }
  getFlightByNumber(flightNumber:string):Flight | null{
    const flight=this.flightsSubject.getValue().find(flight=>flight.flightNumber===flightNumber);
    if(!flight){
      return null;
    }
    return flight;

  }

  deleteFlight(flightNumber:string):Observable<void>{
    return this.http.delete<void>(
      `${this.apiUrl}/${encodeURIComponent(flightNumber)}`
    ).pipe(tap(()=>{
      const newFlights=this.flightsSubject.getValue().filter(flight=>flight.flightNumber!==flightNumber);
      this.flightsSubject.next(newFlights);
    }))
    
     

  }

addFlight(newFlight: Flight): Observable<Flight> {
  return this.http.post<Flight>(this.apiUrl, newFlight).pipe(
    tap(savedFlight => {
      const newFlights = [
        ...this.flightsSubject.getValue(),
        savedFlight
      ];

      this.flightsSubject.next(newFlights);
    })
  );
}

 updateFlight(updatedFlight: Flight): Observable<Flight> {
  const request = {
    origin: updatedFlight.origin,
    destination: updatedFlight.destination,
    status: updatedFlight.status,
    departureTime: updatedFlight.departureTime,
    gate: updatedFlight.gate
  };

  return this.http.put<Flight>(
    `${this.apiUrl}/${encodeURIComponent(updatedFlight.flightNumber)}`,
    request
  ).pipe(
    tap(savedFlight => {
      const newFlights = this.flightsSubject.getValue().map(flight =>
        flight.flightNumber === savedFlight.flightNumber
          ? savedFlight
          : flight
      );

      this.flightsSubject.next(newFlights);
    })
  );
}




 loadFlights(): Observable<readonly Flight[]> {

  return this.http.get<readonly Flight[]>(this.apiUrl).pipe(
    tap(flights=>this.flightsSubject.next(flights))
  );

   
}
 fetchFlightByNumber(flightNumber:string):Observable<Flight>{
  return this.http.get<Flight>(`${this.apiUrl}/${encodeURIComponent(flightNumber)}`)

 }




}
