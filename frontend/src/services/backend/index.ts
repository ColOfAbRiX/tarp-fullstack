import axios from "axios";

export function useBackend() {
    const backend =
        axios.create({
            // During development, the backend is hosted at 'localhost:8080'
            baseURL: import.meta.env.DEV ? "http://localhost:8080" : undefined,
        });

    return { backend };
}
