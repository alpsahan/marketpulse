import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import "./AlertsPage.css";
import { Brand } from "../components/Brand";
import { getMarketPrice } from "../services/marketService";
import type { MarketPrice } from "../types/market";

import {
    createAlert,
    deleteAlert,
    getAlerts,
    updateAlert
} from "../services/alertService";

import type {
    AlertCondition,
    PriceAlert
} from "../types/alert";

function AlertsPage() {
    const [alerts, setAlerts] = useState<PriceAlert[]>([]);
    const [error, setError] = useState("");

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoadingAlerts, setIsLoadingAlerts] = useState(false);

    const [deletingAlertId, setDeletingAlertId] =
        useState<number | null>(null);

    // Create / Edit
    const [symbol, setSymbol] = useState("");
    const [targetPrice, setTargetPrice] = useState("");
    const [condition, setCondition] =
        useState<AlertCondition>("ABOVE");
    const [active, setActive] = useState(true);

    const [editingAlertId, setEditingAlertId] =
        useState<number | null>(null);

    // Filters
    const [filterSymbol, setFilterSymbol] = useState("");
    const [filterActive, setFilterActive] = useState("");
    const [filterCondition, setFilterCondition] = useState("");
    const [sortOption, setSortOption] = useState("");

    // Pagination
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const PAGE_SIZE = 5;

    const [marketPrices, setMarketPrices] = useState<MarketPrice[]>([]);
    const [marketError, setMarketError] = useState("");

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        navigate("/login");
    };

    const loadAlerts = async (page: number = currentPage) => {
        setIsLoadingAlerts(true);

        try {
            const response = await getAlerts({
                symbol:
                    filterSymbol.trim() !== ""
                        ? filterSymbol.trim().toUpperCase()
                        : undefined,

                active:
                    filterActive === ""
                        ? undefined
                        : filterActive === "true",

                condition:
                    filterCondition === ""
                        ? undefined
                        : filterCondition as AlertCondition,

                page,
                size: PAGE_SIZE,

                sort:
                    sortOption !== ""
                        ? sortOption
                        : undefined,
            });

            setAlerts(response.content);
            setCurrentPage(response.page);
            setTotalPages(response.totalPages);
            setError("");

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        } finally {
            setIsLoadingAlerts(false);
        }
    };

    useEffect(() => {
        const loadInitialAlerts = async () => {
            try {
                const response = await getAlerts({
                    page: 0,
                    size: PAGE_SIZE,
                });

                setAlerts(response.content);
                setCurrentPage(response.page);
                setTotalPages(response.totalPages);
                setError("");

            } catch (error) {
                if (error instanceof Error) {
                    setError(error.message);
                }
            }
        };

        loadInitialAlerts();
    }, []);

    useEffect(() => {
        const loadMarketPrices = async () => {
            try {
                const prices = await Promise.all([
                    getMarketPrice("BTCUSDT"),
                    getMarketPrice("ETHUSDT"),
                    getMarketPrice("BNBUSDT"),
                ]);

                setMarketPrices(prices);
                setMarketError("");

            } catch (error) {
                if (error instanceof Error) {
                    setMarketError(error.message);
                }
            }
        };

        loadMarketPrices();

        const interval = setInterval(
            loadMarketPrices,
            10000
        );

        return () => {
            clearInterval(interval);
        };
    }, []);

    const handleSubmitAlert = async (
        event: FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        setIsSubmitting(true);

        try {
            if (editingAlertId !== null) {
                await updateAlert(
                    editingAlertId,
                    {
                        symbol,
                        targetPrice: Number(targetPrice),
                        condition,
                        active,
                    }
                );

                setEditingAlertId(null);

            } else {
                await createAlert({
                    symbol,
                    targetPrice: Number(targetPrice),
                    condition,
                });
            }

            setSymbol("");
            setTargetPrice("");
            setCondition("ABOVE");
            setActive(true);
            setError("");

            await loadAlerts();

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDeleteAlert = async (id: number) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this alert?"
        );

        if (!confirmed) {
            return;
        }

        setDeletingAlertId(id);

        try {
            await deleteAlert(id);

            await loadAlerts();

            setError("");

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        } finally {
            setDeletingAlertId(null);
        }
    };

    const handleEditAlert = (alert: PriceAlert) => {
        setEditingAlertId(alert.id);

        setSymbol(alert.symbol);
        setTargetPrice(alert.targetPrice.toString());
        setCondition(alert.condition);
        setActive(alert.active);
    };

    const handleCancelEdit = () => {
        setEditingAlertId(null);

        setSymbol("");
        setTargetPrice("");
        setCondition("ABOVE");
        setActive(true);
        setError("");
    };

    const handleFilter = async () => {
        await loadAlerts(0);
    };

    const handleClearFilters = async () => {
        setFilterSymbol("");
        setFilterActive("");
        setFilterCondition("");
        setSortOption("");

        try {
            const response = await getAlerts({
                page: 0,
                size: PAGE_SIZE,
            });

            setAlerts(response.content);
            setCurrentPage(response.page);
            setTotalPages(response.totalPages);
            setError("");

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        }
    };

    return (
        <div className="alerts-page">
            <a
                className="skip-link"
                href="#dashboard"
            >
                Skip to dashboard
            </a>

            <header className="topbar">
                <div className="topbar-inner">
                    <Brand />

                    <span className="nav-current">
                        Overview
                    </span>

                    <button
                        type="button"
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Logout
                        <span aria-hidden="true">
                            ↗
                        </span>
                    </button>
                </div>
            </header>

            <main
                className="alerts-container"
                id="dashboard"
            >
                <header className="page-header">
                    <div>
                        <span className="eyebrow">
                            MARKET WORKSPACE
                        </span>

                        <h1>
                            Market overview
                            <span className="heading-dot">
                                .
                            </span>
                        </h1>

                        <p>
                            Keep an eye on the market. Make your next move count.
                        </p>
                    </div>

                    <span className="refresh-note">
                        <span className="status-dot" />
                        Prices refresh every 10s
                    </span>
                </header>

                {marketError && (
                    <div
                        className="error-message"
                        role="alert"
                    >
                        {marketError}
                    </div>
                )}

                <section
                    className="market-grid"
                    aria-label="Market prices"
                >
                    {marketPrices.length === 0 ? (
                        ["BTC", "ETH", "BNB"].map((ticker) => (
                            <div
                                className="market-card"
                                key={ticker}
                            >
                                <span className="market-symbol">
                                    {ticker}

                                    <span className="market-pair">
                                        / USDT
                                    </span>
                                </span>

                                <div
                                    className={
                                        marketError
                                            ? "market-unavailable"
                                            : "skeleton"
                                    }
                                >
                                    {marketError ? (
                                        "Price unavailable"
                                    ) : (
                                        <span className="sr-only">
                                            Loading price
                                        </span>
                                    )}
                                </div>
                            </div>
                        ))
                    ) : (
                        marketPrices.map((market) => {
                            const ticker =
                                market.symbol.replace(
                                    "USDT",
                                    ""
                                );

                            const names: Record<string, string> = {
                                BTC: "Bitcoin",
                                ETH: "Ethereum",
                                BNB: "BNB"
                            };

                            return (
                                <article
                                    className="market-card"
                                    key={market.symbol}
                                >
                                    <div className="market-card-top">
                                        <div className="asset-identity">
                                            <span
                                                className={`coin coin-${ticker.toLowerCase()}`}
                                                aria-hidden="true"
                                            >
                                                {ticker === "BTC"
                                                    ? "₿"
                                                    : ticker === "ETH"
                                                        ? "Ξ"
                                                        : "◇"}
                                            </span>

                                            <div>
                                                <div className="market-symbol">
                                                    {names[ticker] || ticker}
                                                </div>

                                                <div className="market-pair">
                                                    {ticker} / USDT
                                                </div>
                                            </div>
                                        </div>

                                        <span className="quote-label">
                                            SPOT PRICE
                                        </span>
                                    </div>

                                    <div className="market-price">
                                        $
                                        {market.price.toLocaleString(
                                            "en-US",
                                            {
                                                minimumFractionDigits: 2,
                                                maximumFractionDigits: 2
                                            }
                                        )}

                                        <span>
                                            USDT
                                        </span>
                                    </div>

                                    <div className="market-card-footer">
                                        <span>
                                            Market price
                                        </span>

                                        <span className="market-ticker">
                                            {market.symbol}
                                        </span>
                                    </div>
                                </article>
                            );
                        })
                    )}
                </section>

                <section
                    className="card create-card"
                    aria-labelledby="create-title"
                >
                    <div className="section-heading">
                        <div
                            className="section-icon"
                            aria-hidden="true"
                        >
                            ＋
                        </div>

                        <div>
                            <h2 id="create-title">
                                {editingAlertId !== null
                                    ? "Edit alert"
                                    : "Create a price alert"}
                            </h2>

                            <p>
                                {editingAlertId !== null
                                    ? "Adjust your target and update this alert."
                                    : "Set a target. Keep your next opportunity in sight."}
                            </p>
                        </div>

                        <span className="section-caption">
                            YOUR STRATEGY, YOUR RULES
                        </span>
                    </div>

                    <form
                        onSubmit={handleSubmitAlert}
                        className="create-form"
                    >
                        <label className="field">
                            Trading pair

                            <input
                                type="text"
                                placeholder="e.g. BTCUSDT"
                                value={symbol}
                                onChange={(event) =>
                                    setSymbol(event.target.value)
                                }
                                required
                            />
                        </label>

                        <label className="field">
                            Target price

                            <input
                                type="number"
                                placeholder="Enter target price"
                                value={targetPrice}
                                onChange={(event) =>
                                    setTargetPrice(
                                        event.target.value
                                    )
                                }
                                required
                            />
                        </label>

                        <label className="field">
                            Trigger condition

                            <select
                                value={condition}
                                onChange={(event) =>
                                    setCondition(
                                        event.target.value as AlertCondition
                                    )
                                }
                            >
                                <option value="ABOVE">
                                    Price goes above
                                </option>

                                <option value="BELOW">
                                    Price goes below
                                </option>
                            </select>
                        </label>

                        {editingAlertId !== null && (
                            <label className="field">
                                Status

                                <select
                                    value={
                                        active
                                            ? "true"
                                            : "false"
                                    }
                                    onChange={(event) =>
                                        setActive(
                                            event.target.value === "true"
                                        )
                                    }
                                >
                                    <option value="true">
                                        Active
                                    </option>

                                    <option value="false">
                                        Inactive
                                    </option>
                                </select>
                            </label>
                        )}

                        <div className="form-actions">
                            <button
                                type="submit"
                                className="primary-button"
                                disabled={isSubmitting}
                            >
                                {isSubmitting
                                    ? "Saving..."
                                    : editingAlertId !== null
                                        ? "Update alert"
                                        : "+ Create alert"}
                            </button>

                            {editingAlertId !== null && (
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={handleCancelEdit}
                                >
                                    Cancel
                                </button>
                            )}
                        </div>
                    </form>
                </section>

                <section
                    className="table-card"
                    aria-labelledby="alerts-title"
                >
                    <div className="table-heading">
                        <div>
                            <span className="eyebrow">
                                ALERT MANAGEMENT
                            </span>

                            <h2 id="alerts-title">
                                Your price alerts
                            </h2>

                            <p>
                                Fine-tune your targets and stay in control.
                            </p>
                        </div>

                        <span className="count-pill">
                            {alerts.length} on this page
                        </span>
                    </div>

                    <form
                        className="filter-row"
                        aria-label="Filter alerts"
                        onSubmit={(event) => {
                            event.preventDefault();
                            void handleFilter();
                        }}
                    >
                        <label className="field">
                            Symbol

                            <input
                                type="text"
                                placeholder="Search symbol..."
                                value={filterSymbol}
                                onChange={(event) =>
                                    setFilterSymbol(
                                        event.target.value
                                    )
                                }
                            />
                        </label>

                        <label className="field">
                            Status

                            <select
                                value={filterActive}
                                onChange={(event) =>
                                    setFilterActive(
                                        event.target.value
                                    )
                                }
                            >
                                <option value="">
                                    All statuses
                                </option>

                                <option value="true">
                                    Active
                                </option>

                                <option value="false">
                                    Inactive
                                </option>
                            </select>
                        </label>

                        <label className="field">
                            Condition

                            <select
                                value={filterCondition}
                                onChange={(event) =>
                                    setFilterCondition(
                                        event.target.value
                                    )
                                }
                            >
                                <option value="">
                                    All conditions
                                </option>

                                <option value="ABOVE">
                                    Above
                                </option>

                                <option value="BELOW">
                                    Below
                                </option>
                            </select>
                        </label>

                        <label className="field">
                            Sort by

                            <select
                                value={sortOption}
                                onChange={(event) =>
                                    setSortOption(
                                        event.target.value
                                    )
                                }
                            >
                                <option value="">
                                    Default order
                                </option>

                                <option value="targetPrice,asc">
                                    Price: low to high
                                </option>

                                <option value="targetPrice,desc">
                                    Price: high to low
                                </option>

                                <option value="symbol,asc">
                                    Symbol: A–Z
                                </option>

                                <option value="symbol,desc">
                                    Symbol: Z–A
                                </option>
                            </select>
                        </label>

                        <div className="form-actions">
                            <button
                                type="submit"
                                className="filter-button"
                            >
                                Apply filters
                            </button>

                            <button
                                type="button"
                                className="clear-button"
                                onClick={handleClearFilters}
                            >
                                Reset
                            </button>
                        </div>
                    </form>

                    {error && (
                        <div
                            className="error-message table-error"
                            role="alert"
                        >
                            {error}
                        </div>
                    )}

                    {isLoadingAlerts ? (
                        <div
                            className="empty-state"
                            role="status"
                        >
                            <span className="loader" />

                            <h3>
                                Loading your alerts
                            </h3>

                            <p>
                                Getting your latest price targets.
                            </p>
                        </div>
                    ) : alerts.length === 0 ? (
                        <div className="empty-state">
                            <span
                                className="empty-icon"
                                aria-hidden="true"
                            >
                                ◎
                            </span>

                            <h3>
                                No alerts to display
                            </h3>

                            <p>
                                Create your first price alert above, or adjust your filters.
                            </p>
                        </div>
                    ) : (
                        <div
                            className="table-scroll"
                            tabIndex={0}
                            role="region"
                            aria-label="Price alerts table"
                        >
                            <table className="alert-table">
                                <thead>
                                    <tr>
                                        <th scope="col">
                                            Trading pair
                                        </th>

                                        <th scope="col">
                                            Target price
                                        </th>

                                        <th scope="col">
                                            Condition
                                        </th>

                                        <th scope="col">
                                            Status
                                        </th>

                                        <th
                                            scope="col"
                                            className="actions-heading"
                                        >
                                            Actions
                                        </th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {alerts.map((alert) => (
                                        <tr
                                            key={alert.id}
                                            className={
                                                editingAlertId === alert.id
                                                    ? "editing-row"
                                                    : undefined
                                            }
                                        >
                                            <td>
                                                <div className="table-asset">
                                                    <span
                                                        className="asset-avatar"
                                                        aria-hidden="true"
                                                    >
                                                        {alert.symbol.slice(
                                                            0,
                                                            1
                                                        )}
                                                    </span>

                                                    <div>
                                                        <strong>
                                                            {alert.symbol}
                                                        </strong>

                                                        <span className="asset-subtitle">
                                                            Price alert #{alert.id}
                                                        </span>
                                                    </div>
                                                </div>
                                            </td>

                                            <td className="price-cell">
                                                {alert.targetPrice.toLocaleString(
                                                    "en-US",
                                                    {
                                                        maximumFractionDigits: 8
                                                    }
                                                )}
                                            </td>

                                            <td>
                                                <span
                                                    className={`condition condition-${alert.condition.toLowerCase()}`}
                                                >
                                                    <span aria-hidden="true">
                                                        {alert.condition === "ABOVE"
                                                            ? "↗"
                                                            : "↘"}
                                                    </span>

                                                    {" "}
                                                    {alert.condition}
                                                </span>
                                            </td>

                                            <td>
                                                <span
                                                    className={
                                                        alert.active
                                                            ? "status status-active"
                                                            : "status status-inactive"
                                                    }
                                                >
                                                    <span className="status-dot" />

                                                    {alert.active
                                                        ? "Active"
                                                        : "Inactive"}
                                                </span>
                                            </td>

                                            <td>
                                                <div className="actions">
                                                    <button
                                                        type="button"
                                                        className="edit-button"
                                                        onClick={() =>
                                                            handleEditAlert(
                                                                alert
                                                            )
                                                        }
                                                        aria-label={`Edit alert ${alert.id}`}
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="danger-button"
                                                        disabled={
                                                            deletingAlertId === alert.id
                                                        }
                                                        onClick={() =>
                                                            handleDeleteAlert(
                                                                alert.id
                                                            )
                                                        }
                                                        aria-label={`Delete alert ${alert.id}`}
                                                    >
                                                        {deletingAlertId === alert.id
                                                            ? "Deleting..."
                                                            : "Delete"}
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    {totalPages > 0 && (
                        <nav
                            className="pagination"
                            aria-label="Alerts pagination"
                        >
                            <span>
                                Page{" "}
                                <strong>
                                    {currentPage + 1}
                                </strong>
                                {" "}of{" "}
                                <strong>
                                    {totalPages}
                                </strong>
                            </span>

                            <div>
                                <button
                                    type="button"
                                    disabled={
                                        currentPage === 0 ||
                                        isLoadingAlerts
                                    }
                                    onClick={() =>
                                        loadAlerts(
                                            currentPage - 1
                                        )
                                    }
                                >
                                    ← Previous
                                </button>

                                <button
                                    type="button"
                                    disabled={
                                        currentPage + 1 >= totalPages ||
                                        isLoadingAlerts
                                    }
                                    onClick={() =>
                                        loadAlerts(
                                            currentPage + 1
                                        )
                                    }
                                >
                                    Next →
                                </button>
                            </div>
                        </nav>
                    )}
                </section>

                <footer className="dashboard-footer">
                    <span>
                        MarketPulse
                        {" "}
                        <span className="footer-divider">
                            /
                        </span>
                        {" "}
                        Cryptocurrency intelligence
                    </span>

                    <span>
                        Built for a market that never sleeps.
                    </span>
                </footer>
            </main>
        </div>
    );
}

export default AlertsPage;