interface Window {
    native: {
        onmessage: (event) => void
        postMessage: (msg: string) => void
    }
}