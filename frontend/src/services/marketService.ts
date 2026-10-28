import type { MarketPrice } from "../types/market";

const BASE_URL =
    import.meta.env.VITE_API_BASE_URL ??
    "http://localhost:8080";

export async function getMarketPrice(
    symbol: string
): Promise<MarketPrice> {

    const response = await fetch(
        `${BASE_URL}/api/v1/market/prices/${symbol}`
    );

    if (!response.ok) {
        throw new Error(
            `Failed to fetch ${symbol} price`
        );
    }

    return response.json();
}