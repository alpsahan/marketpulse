import {
    useState,
    type FormEvent
} from "react";

import {
    Link,
    useNavigate
} from "react-router-dom";

import { register } from "../services/authService";

import "./RegisterPage.css";

function RegisterPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] =
        useState("");

    const [showPassword, setShowPassword] =
        useState(false);

    const [showConfirmPassword, setShowConfirmPassword] =
        useState(false);

    const [error, setError] = useState("");

    const [isSubmitting, setIsSubmitting] =
        useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (
        event: FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        setError("");

        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        setIsSubmitting(true);

        try {
            await register({
                email,
                password,
            });

            navigate("/login");

        } catch (error) {
            if (error instanceof Error) {
                setError(error.message);
            }
        }

        finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="register-page">
            <div className="register-card">

                <div className="register-brand">
                    <h1>MarketPulse</h1>

                    <p>
                        Cryptocurrency price alert platform
                    </p>
                </div>

                <h2 className="register-title">
                    Create your account
                </h2>

                <form
                    className="register-form"
                    onSubmit={handleSubmit}
                >

                    <div className="register-field">
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

                    <div className="register-field">
                        <label htmlFor="password">
                            Password
                        </label>

                        <div className="register-password-wrapper">

                            <input
                                id="password"
                                type={
                                    showPassword
                                        ? "text"
                                        : "password"
                                }
                                value={password}
                                onChange={(event) =>
                                    setPassword(
                                        event.target.value
                                    )
                                }
                                required
                            />

                            <button
                                type="button"
                                className="register-password-toggle"
                                onClick={() =>
                                    setShowPassword(
                                        (current) => !current
                                    )
                                }
                                aria-label="Show or hide password"
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

                    <div className="register-field">
                        <label htmlFor="confirmPassword">
                            Confirm Password
                        </label>

                        <div className="register-password-wrapper">

                            <input
                                id="confirmPassword"
                                type={
                                    showConfirmPassword
                                        ? "text"
                                        : "password"
                                }
                                value={confirmPassword}
                                onChange={(event) =>
                                    setConfirmPassword(
                                        event.target.value
                                    )
                                }
                                required
                            />

                            <button
                                type="button"
                                className="register-password-toggle"
                                onClick={() =>
                                    setShowConfirmPassword(
                                        (current) => !current
                                    )
                                }
                                aria-label="Show or hide password"
                            >
                                {showConfirmPassword ? (
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
                        <div className="register-error">
                            {error}
                        </div>
                    )}

                    <button
                        className="register-button"
                        type="submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Creating account..."
                            : "Create Account"}
                    </button>

                </form>

                <div className="register-login">
                    Already have an account?{" "}

                    <Link to="/login">
                        Login
                    </Link>
                </div>

            </div>
        </div>
    );
}

export default RegisterPage;