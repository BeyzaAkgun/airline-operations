import { Routes } from '@angular/router';
import { Dashboard } from './features/dashboard/dashboard';
import { FlightForm } from './features/flights/flight-form/flight-form';

export const routes: Routes = [
    {
        path:'',
        component:Dashboard
    },
    {
        path:"flights/new",
        component:FlightForm
    },
    {
        path:"flights/:number/edit",
        component:FlightForm
    },
    {
        path:'**',
        redirectTo:''

    }

  
];
