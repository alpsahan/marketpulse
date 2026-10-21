import { useState, type FormEvent } from "react";
import {
    Link,
    useNavigate
} from "react-router-dom";

import { login } from "../services/authService";


import "./LoginPage.css";

function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const [showPassword, setShowPassword] = useState(false);

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (
        event: FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        setError("");
        setIsSubmitting(true);

        try {
            const response = await login({
                email,
                password,
            });

            localStorage.setItem(
                "accessToken",
                response.token
            );

            navigate("/alerts");

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="login-page">
            <div className="login-card">

                <div className="login-brand">
                    <h1>MarketPulse</h1>

                    <p>
                        Cryptocurrency price alert platform
                    </p>
                </div>

                <h2 className="login-title">
                    Sign in to your account
                </h2>

                <form
                    className="login-form"
                    onSubmit={handleSubmit}
                >

                    <div className="login-field">
                        <label htmlFor="email">
                            Email
                        </label>

                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            required
                        />
                    </div>

                    <div className="login-field">
                        <label htmlFor="password">
                            Password
                        </label>

                        <div className="password-wrapper">
                            <input
                                id="password"
                                type={showPassword ? "text" : "password"}
                                placeholder="Enter your password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                required
                            />

                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() =>
                                    setShowPassword(
                                        (current) => !current
                                    )
                                }
                                aria-label={
                                    showPassword
                                        ? "Hide password"
                                        : "Show password"
                                }
                            >
                                {showPassword ? (
                                    <svg
                                        viewBox="0 0 24 24"
                                        width="20"
                                        height="20"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                    >
                                        <path d="M3 3l18 18" />
                                        <path d="M10.6 10.6a2 2 0 002.8 2.8" />
                                        <path d="M9.9 4.2A10.8 10.8 0 0112 4c5 0 9 4 10 8a12 12 0 01-2.1 4.2" />
                                        <path d="M6.6 6.6C4.3 8 2.7 10 2 12c1 4 5 8 10 8 1.4 0 2.7-.3 3.9-.8" />
                                    </svg>
                                ) : (
                                    <svg
                                        viewBox="0 0 24 24"
                                        width="20"
                                        height="20"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                    >
                                        <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" />
                                        <circle
                                            cx="12"
                                            cy="12"
                                            r="3"
                                        />
                                    </svg>
                                )}
                            </button>
                        </div>
                    </div>

                    {error && (
                        <div className="login-error">
                            {error}
                        </div>
                    )}

                    <button
                        className="login-button"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Signing in..."
                            : "Login"}
                    </button>

                    <div className="login-register">
                        Don't have an account?{" "}

                        <Link to="/register">
                            Register
                        </Link>
                    </div>

                </form>

                <div className="login-footer">
                    MarketPulse
                </div>



            </div>
        </div>
    );
}

export default LoginPage;