import type {
    LoginRequest,
    LoginResponse,
    RegisterRequest,
    UserResponse
} from "../types/auth";

const BASE_URL =
    import.meta.env.VITE_API_BASE_URL ??
    "http://localhost:8080";

export async function login(
    request: LoginRequest
): Promise<LoginResponse> {

    const response = await fetch(
        `${BASE_URL}/api/v1/auth/login`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        }
    );

    if (!response.ok) {
        throw new Error("Invalid email or password");
    }

    return response.json();
}

export async function register(
    request: RegisterRequest
): Promise<UserResponse> {

    const response = await fetch(
        `${BASE_URL}/api/v1/users`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        }
    );

    if (!response.ok) {
        const data = await response.json();

        throw new Error(
            data.message || "Registration failed"
        );
    }

    return response.json();
}