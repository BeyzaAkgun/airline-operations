import type { Flight } from '../models/flight.model';

export const MOCK_FLIGHTS:readonly Flight[]=[{
    flightNumber:"TX23",
    origin:"Istanbul",
    destination:"Antalya",
    status:"DELAYED",
    departureTime: "2026-09-10T10:00:00Z",
    gate: "A12"

},
 {
        flightNumber: "A2X7",
        origin: "Berlin",
        destination: "Izmir",
        status: "CANCELED",
        departureTime: "2026-09-10T08:30:00Z",
        gate: "B04"
    },
    {
        flightNumber: "C4K6",
        origin: "Rome",
        destination: "Los Angeles",
        status: "SCHEDULED",
        departureTime: "2026-09-10T14:00:00Z",
        gate: ""
    },
    {
        flightNumber: "D8K2",
        origin: "Madrid",
        destination: "Paris",
        status:"CANCELED",
        departureTime: "2026-09-10T07:15:00Z",
        gate: "C08"
    },
    {
        flightNumber: "F54K",
        origin: "Seoul",
        destination: "Tokyo",
        status: "SCHEDULED",
        departureTime: "2026-09-10T11:45:00Z",
        gate: "D02"
    }
];