export type AlertCondition = "ABOVE" | "BELOW";

export interface PriceAlert {
    id: number;
    symbol: string;
    targetPrice: number;
    condition: AlertCondition;
    active: boolean;
}

export interface PageResponse<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
}

export interface CreatePriceAlertRequest {
    symbol: string;
    targetPrice: number;
    condition: AlertCondition;
}