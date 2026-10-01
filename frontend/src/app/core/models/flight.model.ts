export type FlightStatus= "DELAYED" | "SCHEDULED" |"CANCELED";

export interface Flight{
    readonly flightNumber:string,
    origin:string,
    destination:string,
    status:FlightStatus,
    departureTime:string,
    gate:string

}