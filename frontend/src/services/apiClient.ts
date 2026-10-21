const BASE_URL = "http://localhost:8080";

export async function authenticatedFetch(
    endpoint: string,
    options: RequestInit = {}
): Promise<Response> {

    const token = localStorage.getItem("accessToken");

    if (!token) {
        window.location.replace("/login");
        throw new Error("No access token");
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers: {
            ...options.headers,
            Authorization: `Bearer ${token}`,
        },
    });

    if (response.status === 401) {
        localStorage.removeItem("accessToken");

        window.location.replace("/login");

        throw new Error("Session expired");
    }

    return response;
}