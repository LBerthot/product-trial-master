import { http } from "./http";

export type LoginResponse = {
    token: string;
};

export type RegisterPayload = {
    username: string;
    firstname: string;
    email: string;
    password: string;
};

export async function login(email: string, password: string): Promise<LoginResponse> {
    const response = await http.post<LoginResponse>("/token", { 
        email,
        password 
    });
    return response.data;
}

export async function register(payload: RegisterPayload): Promise<unknown> {
    const response = await http.post("/account", payload);
    return response.data;
}