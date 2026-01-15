import { http } from "./http";

export type Account = {
    id: number;
    username: string;
    email: string;
};

export async function getAccount(userId: number): Promise<Account> {
    const response = await http.get(`/account/${userId}`);
    return response.data;
}