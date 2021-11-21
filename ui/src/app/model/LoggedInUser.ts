import { MediaFile } from "./MediaFile";

export class LoggedInUser {
    userId: number;
    organisationId: number;
    appManager: boolean;
    email: string;
    jwtToken: string;
    timeZone: string;
    location: string;
    department: string;
    organisationLogo: MediaFile;
    profileImage: MediaFile;
    modulePermissions: Record<string, string>
}