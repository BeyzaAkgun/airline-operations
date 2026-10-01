export interface ApiErrorResponse{
    timestamp:string,
    status:number,
    message:string,
    path:string,
    fieldErrors:Record<string,string>

}

