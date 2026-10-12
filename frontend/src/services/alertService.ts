import type {
    CreatePriceAlertRequest,
    PageResponse,
    PriceAlert
} from "../types/alert";

import { authenticatedFetch } from "./apiClient";

export async function getAlerts(): Promise<PageResponse<PriceAlert>> {
    const response = await authenticatedFetch(
        "/api/v1/alerts?page=0&size=5"
    );

    if (!response.ok) {
        throw new Error("Failed to fetch alerts");
    }

    return response.json();
}

export async function createAlert(
    request: CreatePriceAlertRequest
): Promise<PriceAlert> {

    const response = await authenticatedFetch("/api/v1/alerts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
    });

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Failed to create alert");
    }

    return response.json();
}