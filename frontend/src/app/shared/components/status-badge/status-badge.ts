import { Component,Input } from '@angular/core';
import type { FlightStatus } from '../../../core/models/flight.model';

@Component({
  selector: 'app-status-badge',
  imports: [],
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.scss',
})
export class StatusBadge {
  @Input({required:true}) status!:FlightStatus
}
