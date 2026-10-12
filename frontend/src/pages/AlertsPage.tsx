import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { createAlert, getAlerts } from "../services/alertService";
import type { AlertCondition, PriceAlert } from "../types/alert";

function AlertsPage() {
    const [alerts, setAlerts] = useState<PriceAlert[]>([]);
    const [error, setError] = useState("");

    const [symbol, setSymbol] = useState("");
    const [targetPrice, setTargetPrice] = useState("");
    const [condition, setCondition] = useState<AlertCondition>("ABOVE");

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        navigate("/login");
    };

    const handleCreateAlert = async (
        event: React.FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        try {
            const newAlert = await createAlert({
                symbol,
                targetPrice: Number(targetPrice),
                condition,
            });

            setAlerts((currentAlerts) => [
                newAlert,
                ...currentAlerts,
            ]);

            setSymbol("");
            setTargetPrice("");
            setCondition("ABOVE");
            setError("");
        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        }
    };

    useEffect(() => {
        const loadAlerts = async () => {
            try {
                const response = await getAlerts();

                setAlerts(response.content);
            } catch (error) {
                if (error instanceof Error) {
                    setError(error.message);
                }
            }
        };

        loadAlerts();
    }, []);

    return (
        <div>
            <h1>MarketPulse</h1>
            <h2>My Alerts</h2>

            <button onClick={handleLogout}>
                Logout
            </button>

            <form onSubmit={handleCreateAlert}>
                <h3>Create Alert</h3>

                <input
                    type="text"
                    placeholder="BTCUSDT"
                    value={symbol}
                    onChange={(event) => setSymbol(event.target.value)}
                    required
                />

                <input
                    type="number"
                    placeholder="Target price"
                    value={targetPrice}
                    onChange={(event) => setTargetPrice(event.target.value)}
                    required
                />

                <select
                    value={condition}
                    onChange={(event) =>
                        setCondition(event.target.value as AlertCondition)
                    }
                >
                    <option value="ABOVE">Above</option>
                    <option value="BELOW">Below</option>
                </select>

                <button type="submit">
                    Create Alert
                </button>
            </form>

            {error && <p>{error}</p>}

            {alerts.length === 0 ? (
                <p>No alerts found.</p>
            ) : (
                <table>
                    <thead>
                        <tr>
                            <th>Symbol</th>
                            <th>Target Price</th>
                            <th>Condition</th>
                            <th>Active</th>
                        </tr>
                    </thead>

                    <tbody>
                        {alerts.map((alert) => (
                            <tr key={alert.id}>
                                <td>{alert.symbol}</td>
                                <td>{alert.targetPrice}</td>
                                <td>{alert.condition}</td>
                                <td>{alert.active ? "Yes" : "No"}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default AlertsPage;