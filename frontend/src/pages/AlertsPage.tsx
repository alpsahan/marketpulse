import { useEffect, useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import "./AlertsPage.css";
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
            <div className="alerts-container">

                <header className="page-header">
                    <div>
                        <h1>MarketPulse</h1>
                        <p>Manage your cryptocurrency price alerts</p>
                    </div>

                    {marketError && (
                        <div className="error-message">
                            {marketError}
                        </div>
                    )}

                    <div className="market-grid">
                        {marketPrices.map((market) => (
                            <div
                                className="market-card"
                                key={market.symbol}
                            >
                                <div className="market-symbol">
                                    {market.symbol.replace(
                                        "USDT",
                                        ""
                                    )}
                                </div>

                                <div className="market-pair">
                                    {market.symbol}
                                </div>

                                <div className="market-price">
                                    $
                                    {market.price.toLocaleString(
                                        "en-US",
                                        {
                                            minimumFractionDigits: 2,
                                            maximumFractionDigits: 2,
                                        }
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>

                    <button
                        type="button"
                        className="logout-button"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </header>

                <div className="dashboard-grid">

                    <div className="card">
                        <h3>
                            {editingAlertId !== null
                                ? "Edit Alert"
                                : "Create Alert"}
                        </h3>

                        <form onSubmit={handleSubmitAlert}>
                            <div className="form-row">

                                <input
                                    type="text"
                                    placeholder="BTCUSDT"
                                    value={symbol}
                                    onChange={(event) =>
                                        setSymbol(event.target.value)
                                    }
                                    required
                                />

                                <input
                                    type="number"
                                    placeholder="Target price"
                                    value={targetPrice}
                                    onChange={(event) =>
                                        setTargetPrice(event.target.value)
                                    }
                                    required
                                />

                                <select
                                    value={condition}
                                    onChange={(event) =>
                                        setCondition(
                                            event.target.value as AlertCondition
                                        )
                                    }
                                >
                                    <option value="ABOVE">
                                        Above
                                    </option>

                                    <option value="BELOW">
                                        Below
                                    </option>
                                </select>

                                {editingAlertId !== null && (
                                    <select
                                        value={active ? "true" : "false"}
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
                                )}

                                <button
                                    type="submit"
                                    className="primary-button"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting
                                        ? "Saving..."
                                        : editingAlertId !== null
                                            ? "Update"
                                            : "Create"}
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
                    </div>

                    <div className="card">
                        <h3>Filter Alerts</h3>

                        <div className="form-row">

                            <input
                                type="text"
                                placeholder="Symbol"
                                value={filterSymbol}
                                onChange={(event) =>
                                    setFilterSymbol(event.target.value)
                                }
                            />

                            <select
                                value={filterActive}
                                onChange={(event) =>
                                    setFilterActive(event.target.value)
                                }
                            >
                                <option value="">
                                    All Status
                                </option>

                                <option value="true">
                                    Active
                                </option>

                                <option value="false">
                                    Inactive
                                </option>
                            </select>

                            <select
                                value={filterCondition}
                                onChange={(event) =>
                                    setFilterCondition(event.target.value)
                                }
                            >
                                <option value="">
                                    All Conditions
                                </option>

                                <option value="ABOVE">
                                    Above
                                </option>

                                <option value="BELOW">
                                    Below
                                </option>
                            </select>

                            <select
                                value={sortOption}
                                onChange={(event) =>
                                    setSortOption(event.target.value)
                                }
                            >
                                <option value="">
                                    Default Sort
                                </option>

                                <option value="targetPrice,asc">
                                    Price ↑
                                </option>

                                <option value="targetPrice,desc">
                                    Price ↓
                                </option>

                                <option value="symbol,asc">
                                    Symbol A-Z
                                </option>

                                <option value="symbol,desc">
                                    Symbol Z-A
                                </option>
                            </select>

                            <button
                                type="button"
                                className="primary-button"
                                onClick={handleFilter}
                            >
                                Filter
                            </button>

                            <button
                                type="button"
                                className="secondary-button"
                                onClick={handleClearFilters}
                            >
                                Clear
                            </button>

                        </div>
                    </div>
                </div>

                {error && (
                    <div className="error-message">
                        {error}
                    </div>
                )}

                <div className="table-card">

                    {isLoadingAlerts ? (
                        <div className="empty-state">
                            Loading alerts...
                        </div>
                    ) : alerts.length === 0 ? (
                        <div className="empty-state">
                            No alerts found.
                        </div>
                    ) : (
                        <>
                            {/* mevcut table + pagination */}
                        </>
                    )} : (
                    <>
                        <table className="alert-table">
                            <thead>
                                <tr>
                                    <th>Symbol</th>
                                    <th>Target Price</th>
                                    <th>Condition</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>

                            <tbody>
                                {alerts.map((alert) => (
                                    <tr key={alert.id}>

                                        <td>
                                            <strong>
                                                {alert.symbol}
                                            </strong>
                                        </td>

                                        <td>
                                            {alert.targetPrice}
                                        </td>

                                        <td>
                                            {alert.condition}
                                        </td>

                                        <td>
                                            <span
                                                className={
                                                    alert.active
                                                        ? "status status-active"
                                                        : "status status-inactive"
                                                }
                                            >
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
                                                        handleEditAlert(alert)
                                                    }
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    type="button"
                                                    className="danger-button"
                                                    disabled={deletingAlertId === alert.id}
                                                    onClick={() =>
                                                        handleDeleteAlert(alert.id)
                                                    }
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

                        {totalPages > 0 && (
                            <div className="pagination">

                                <button
                                    type="button"
                                    disabled={
                                        currentPage === 0 ||
                                        isLoadingAlerts
                                    }
                                    onClick={() =>
                                        loadAlerts(currentPage - 1)
                                    }
                                >
                                    Previous
                                </button>

                                <span>
                                    Page {currentPage + 1} / {totalPages}
                                </span>

                                <button
                                    type="button"
                                    disabled={
                                        currentPage + 1 >= totalPages ||
                                        isLoadingAlerts
                                    }
                                    onClick={() =>
                                        loadAlerts(currentPage + 1)
                                    }
                                >
                                    Next
                                </button>

                            </div>
                        )}
                    </>
                    )

                </div>

            </div>
        </div>
    );
}

export default AlertsPage;