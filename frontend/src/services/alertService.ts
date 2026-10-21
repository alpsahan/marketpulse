import type {
    AlertFilters,
    CreatePriceAlertRequest,
    UpdatePriceAlertRequest,
    PageResponse,
    PriceAlert
} from "../types/alert";

import { authenticatedFetch } from "./apiClient";

export async function getAlerts(
    filters: AlertFilters = {}
): Promise<PageResponse<PriceAlert>> {

    const params = new URLSearchParams();

    params.set(
        "page",
        (filters.page ?? 0).toString()
    );

    params.set(
        "size",
        (filters.size ?? 5).toString()
    );

    if (filters.symbol) {
        params.set("symbol", filters.symbol);
    }

    if (filters.active !== undefined) {
        params.set("active", filters.active.toString());
    }

    if (filters.condition) {
        params.set("condition", filters.condition);
    }

    if (filters.sort) {
        params.set("sort", filters.sort);
    }

    const response = await authenticatedFetch(
        `/api/v1/alerts?${params.toString()}`
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

export async function deleteAlert(id: number): Promise<void> {
    const response = await authenticatedFetch(
        `/api/v1/alerts/${id}`,
        {
            method: "DELETE",
        }
    );

    if (!response.ok) {
        throw new Error("Failed to delete alert");
    }
}


export async function updateAlert(
    id: number,
    request: UpdatePriceAlertRequest
): Promise<PriceAlert> {

    const response = await authenticatedFetch(
        `/api/v1/alerts/${id}`,
        {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        }
    );

    if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Failed to update alert");
    }

    return response.json();
}