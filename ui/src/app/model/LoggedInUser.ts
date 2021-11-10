import { MediaFile } from "./MediaFile";

export class LoggedInUser {

    userId: number;
    organisationId: number;
    appManager: boolean;
    email: string;
    jwtToken: string;
    mediaFile: MediaFile;
    modulePermissions: Record<string, string>
}