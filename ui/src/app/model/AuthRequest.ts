export class AuthRequest {
    username: string;
    password: string;
    rememberMe: boolean;

    constructor() {
        this.username = '';
        this.password = '';
        this.rememberMe = true;
    }
}