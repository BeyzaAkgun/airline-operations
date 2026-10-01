import { Component, inject, signal, DestroyRef, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import type { Flight, FlightStatus } from '../../../core/models/flight.model';
import { notBlankValidator } from '../../../core/validators/not-blank.validator';
import { validDateValidator } from '../../../core/validators/valid-date.validator';
import { FlightService } from '../../../core/services/flight';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { isApiErrorResponse } from '../../../core/guards/api-error-response.guard';
import { getErrorMessage } from '../../../core/utils/http-error.util';


@Component({
  selector: 'app-flight-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './flight-form.html',
  styleUrl: './flight-form.scss',
})
export class FlightForm implements OnInit {
  private readonly route: ActivatedRoute = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);
  private readonly flightService = inject(FlightService);

  protected readonly saving=signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly serverFieldErrors=signal<Record<string,string>>({});
  protected readonly successMessage = signal<string | null>(null);
  protected readonly loadingFlight=signal(false);
  readonly isEditMode = signal<boolean>(false);

  protected readonly flightForm = new FormGroup({
    flightNumber: new FormControl<string>('', {
      nonNullable: true,
      validators: [
        notBlankValidator,
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(10),
        (control) => this.isFlightNumberTaken(control.value) ? { duplicate: true } : null,
      ],
    }),
    origin: new FormControl<string>('', {
      nonNullable: true,
      validators: [notBlankValidator, Validators.required,Validators.maxLength(100)],
    }),
    destination: new FormControl<string>('', {
      nonNullable: true,
      validators: [notBlankValidator, Validators.required,Validators.maxLength(100)],
    }),
    status: new FormControl<FlightStatus>('SCHEDULED', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    departureTime: new FormControl<string>('', {
      nonNullable: true,
      validators: [validDateValidator, Validators.required],
    }),
    gate: new FormControl<string>('', { nonNullable: true,validators:[Validators.maxLength(10)] }),
  });

  ngOnInit(): void {
    const flightNum = this.route.snapshot.paramMap.get('number');

    if (flightNum === null) {
      return;
    }

    this.isEditMode.set(true);
    this.errorMessage.set(null);
    this.flightForm.disable();
    
    this.loadingFlight.set(true);
    this.flightService
      .fetchFlightByNumber(flightNum)
      .pipe(takeUntilDestroyed(this.destroyRef),
       finalize(()=>this.loadingFlight.set(false)))
      .subscribe({
        next: (flight) => {
          this.flightForm.patchValue({
            flightNumber: flight.flightNumber,
            origin: flight.origin,
            destination: flight.destination,
            status: flight.status,
            departureTime: this.toLocalDateTime(flight.departureTime),
            gate: flight.gate,
          });

          this.flightForm.enable();
          this.flightForm.controls.flightNumber.disable();
        },
        error: (err: unknown) => {
          this.errorMessage.set(getErrorMessage(err,'Flight could not be loaded.'));
          console.error(err);
        },
        
      });
  }

   private setServerFieldErrors(error:unknown):void{
    if(error instanceof HttpErrorResponse){
      const body:unknown=error.error;

      if(isApiErrorResponse(body)){
        this.serverFieldErrors.set(body.fieldErrors);
      }
    }
   }

  protected onSubmit(): void {
  if (this.saving()) {
    return;
  }

  this.errorMessage.set(null);
  this.successMessage.set(null);
  this.serverFieldErrors.set({});

  if (!this.flightForm.valid) {
    this.flightForm.markAllAsTouched();
    return;
  }

  const formResult = this.flightForm.getRawValue();

  const newFlight: Flight = {
    flightNumber: formResult.flightNumber.trim().toUpperCase(),
    origin: formResult.origin.trim(),
    destination: formResult.destination.trim(),
    status: formResult.status,
    departureTime: new Date(formResult.departureTime).toISOString(),
    gate: formResult.gate.trim()
  };

  this.saving.set(true);

  if (this.isEditMode()) {
    this.flightService.updateFlight(newFlight)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false))
      )
      .subscribe({
        next: () => {
          this.successMessage.set('Flight updated successfully.');
        },
        error: (err: unknown) => {
          this.setServerFieldErrors(err);
          this.errorMessage.set('Flight could not be updated.');
          console.error(err);
        }
      });

    return;
  }

  this.flightService.addFlight(newFlight)
    .pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.saving.set(false))
    )
    .subscribe({
      next: () => {
        this.successMessage.set('Flight added successfully.');
        this.flightForm.reset();
      },
      error: (err: unknown) => {
        this.setServerFieldErrors(err);
        this.errorMessage.set(getErrorMessage(err,'Flight could not be added.'));
        console.error(err);
      }
    });
}



  private toLocalDateTime(value: string): string {
    const date = new Date(value);

    if (isNaN(date.getTime())) {
      return '';
    }

    const pad = (number: number): string => String(number).padStart(2, '0');

    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
  }

  private isFlightNumberTaken(flightNumber: string): boolean {
    const flightNum = flightNumber.trim().toUpperCase();

    if (!flightNum) {
      return false;
    }

    const result = this.flightService.getFlightByNumber(flightNum);
    return !!result;
  }
}