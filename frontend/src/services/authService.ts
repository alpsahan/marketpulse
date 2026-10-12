import type { LoginRequest, LoginResponse } from "../types/auth";

const API_URL = "http://localhost:8080/api/v1/auth";

export async function login(
    request: LoginRequest
): Promise<LoginResponse> {

    const response = await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Login failed");
    }

    return data;
}