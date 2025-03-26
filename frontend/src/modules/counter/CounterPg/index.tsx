import viteLogo from "/vite.svg";
import reactLogo from "@/assets/react.svg";
import { useCounter } from "./useCounter";
import "./Counter.css";

export function CounterPg() {
    const { count, addOne, isProgressing } = useCounter();

    return (
        <>
            <div>
            <a href="https://vitejs.dev" target="_blank">
                <img src={viteLogo} className="logo" alt="Vite logo" />
            </a>
            <a href="https://react.dev" target="_blank">
                <img src={reactLogo} className="logo react" alt="React logo" />
            </a>
            </div>
            <h1>Frontend – TARP Stack ⛺</h1>
            <div className="card">
            <button onClick={() => addOne()} disabled={isProgressing}>
                Count is {count}
            </button>
            <p>
                Edit <code>src/App.tsx</code> and save to test HMR
            </p>
            </div>
            <p className="read-the-docs">
            Click on the Vite and React logos to learn more
            </p>
        </>
    );
}
