import { Component, inject,signal,DestroyRef } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { combineLatest, debounceTime, distinctUntilChanged, map, startWith,finalize } from 'rxjs';

import type { Flight, FlightStatus } from '../../core/models/flight.model';
import { FlightService } from '../../core/services/flight';
import { FlightList } from '../flights/flight-list/flight-list';
import { SummaryCard } from '../../shared/components/summary-card/summary-card';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { getErrorMessage } from '../../core/utils/http-error.util';


@Component({
  selector: 'app-dashboard',
  imports: [FlightList,SummaryCard,AsyncPipe,ReactiveFormsModule,RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private readonly flightService= inject(FlightService);
  private readonly destroyRef=inject(DestroyRef);
  protected readonly errorMessage=signal<string|null>(null);
  protected readonly loadErrorMessage=signal<string|null>(null);
  protected readonly loading=signal<boolean>(true);
  protected readonly flights$=this.flightService.flights$;
  protected readonly dashboardData$=this.flights$.pipe(map(flights=>({
     flights:flights,
     statusCounts:this.countFlightByStatus(flights)
  })));
  
  searchControl=new FormControl("",{nonNullable:true});
  protected readonly searchText$ = this.searchControl.valueChanges.pipe(
    debounceTime(300),
    startWith(this.searchControl.value),
    distinctUntilChanged()
);
  routeControl=new FormControl("",{nonNullable:true})
  protected readonly routeText$=this.routeControl.valueChanges.pipe(
    debounceTime(300),
    startWith(this.routeControl.value),
    distinctUntilChanged()
  )
  statusControl=new FormControl<FlightStatus|"ALL">("ALL",{nonNullable:true})
  selectedStatus$=this.statusControl.valueChanges.pipe(startWith(this.statusControl.value));
  protected readonly filteredFlights$=combineLatest([this.flights$,this.searchText$,this.selectedStatus$,this.routeText$]).pipe(
    map(([flights , searchText,selectedStatus,routeText])=>{return flights.filter(flight=>flight.flightNumber.toUpperCase().includes(searchText.trim().toUpperCase())&& (selectedStatus==="ALL" ||selectedStatus===flight.status) && (
    flight.origin.toUpperCase().includes(routeText.trim().toUpperCase()) ||
    flight.destination.toUpperCase().includes(routeText.trim().toUpperCase())
))})
  )

  ngOnInit():void{
    this.loadFlights();
    
  }

  protected loadFlights(): void {
  this.loading.set(true);
  this.loadErrorMessage.set(null);

  this.flightService.loadFlights()
    .pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.loading.set(false))
    )
    .subscribe({
      error: (err: unknown) => {
        this.loadErrorMessage.set(
          getErrorMessage(err, 'Flights could not be loaded.')
        );
        console.error(err);
      }
    });
}

 



  protected countFlightByStatus(flights:readonly Flight[]):Record<FlightStatus,number>{
    return flights.reduce<Record<FlightStatus,number>>((sum,flight)=>{
      sum[flight.status]+=1;
      return sum;
    },{
      SCHEDULED:0,
      DELAYED:0,
      CANCELED:0
    })

  }
 
  protected onDeleteFlight(flightNumber:string):void{
    this.errorMessage.set(null);

          const userConfirmed = confirm(`Delete flight ${flightNumber}`);
          if(!userConfirmed){
            return;
          }

          this.flightService.deleteFlight(flightNumber)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            error:(err:unknown)=>
            {
              this.errorMessage.set(getErrorMessage(err,'Flight could not be deleted.'));
              console.error(err);
            }
          })

}

  protected clearFilters():void{
    this.routeControl.setValue("");
    this.searchControl.setValue("");
    this.statusControl.setValue("ALL");
  }




  
  
}
