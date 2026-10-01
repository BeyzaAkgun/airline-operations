import { Component,EventEmitter,Input,Output } from '@angular/core';
import type { Flight } from '../../../core/models/flight.model';
import { DatePipe } from '@angular/common';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-flight-list',
  imports: [StatusBadge,DatePipe,RouterLink],
  templateUrl: './flight-list.html',
  styleUrl: './flight-list.scss',
})
export class FlightList {
  @Input({required:true}) flights!:readonly Flight[]
  @Input() emptyMessage: string = "No flights found";
  @Output() deleteRequested=new EventEmitter<string>()
  protected onDelete(flightNumber:string):void{
    this.deleteRequested.emit(flightNumber);

  }
}
