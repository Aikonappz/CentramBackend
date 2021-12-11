import { Incident } from "./Incident";

export class IncidentVO {
    public id: number;
    public incidentNo: string;

    constructor(inc: any) {
        //TODO: have to take care
        //console.log(JSON.stringify(inc));
        //console.log(JSON.parse(JSON.stringify(inc)));
        let incident = JSON.parse(JSON.stringify(inc));
        this.id = incident.id;
        this.incidentNo = incident.incidentNo;
    }
}