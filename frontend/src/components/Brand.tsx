import type { ReactNode } from "react";

export function Brand() {
    return (
        <div className="brand">
            <span
                className="brand-mark"
                aria-hidden="true"
            >
                <svg
                    viewBox="0 0 28 28"
                    fill="none"
                >
                    <path
                        d="M3 16h5l4-9 5 15 3-10h5"
                        stroke="currentColor"
                        strokeWidth="2.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    />
                </svg>
            </span>

            <span>
                Market
                <span className="brand-light">
                    Pulse
                </span>
            </span>
        </div>
    );
}

export function AuthLayout({
    children
}: {
    children: ReactNode;
}) {
    return (
        <main className="auth-layout">

            <section className="auth-story">
                <Brand />

                <div className="auth-story-content">

                    <span className="eyebrow">
                        YOUR MARKET. YOUR SIGNAL.
                    </span>

                    <h1>
                        Stay ahead of
                        <br />
                        your next move.
                    </h1>

                    <p>
                        A clear view of the crypto market.
                        <br />
                        Price alerts built around your strategy.
                    </p>

                    <div
                        className="signal-art"
                        aria-hidden="true"
                    >
                        <div className="signal-ring ring-one" />
                        <div className="signal-ring ring-two" />
                        <div className="signal-ring ring-three" />

                        <span className="signal-core">
                            <svg
                                viewBox="0 0 28 28"
                                fill="none"
                            >
                                <path
                                    d="M3 16h5l4-9 5 15 3-10h5"
                                    stroke="currentColor"
                                    strokeWidth="1.5"
                                />
                            </svg>
                        </span>

                        <span className="signal-label label-btc">
                            BTC
                            <span>
                                Bitcoin
                            </span>
                        </span>

                        <span className="signal-label label-eth">
                            ETH
                            <span>
                                Ethereum
                            </span>
                        </span>

                        <span className="signal-label label-bnb">
                            BNB
                            <span>
                                BNB
                            </span>
                        </span>
                    </div>

                    <div className="auth-features">

                        <span>
                            01
                            <b>
                                Follow the market
                            </b>
                        </span>

                        <span>
                            02
                            <b>
                                Set your price targets
                            </b>
                        </span>

                        <span>
                            03
                            <b>
                                Manage your signals
                            </b>
                        </span>

                    </div>
                </div>

                <p className="auth-story-footer">
                    Precision in every price. Clarity in every decision.
                </p>
            </section>

            <section className="auth-panel">
                {children}

                <p className="auth-disclaimer">
                    Your market workspace, in focus.
                </p>
            </section>

        </main>
    );
}