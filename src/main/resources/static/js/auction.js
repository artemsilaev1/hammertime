document.addEventListener("DOMContentLoaded", function () {
    const lotId = window.lotId;

    const bidForm = document.getElementById("bidForm");
    const bidAmountInput = document.getElementById("bidAmount");
    const bidMessage = document.getElementById("bidMessage");
    const currentPrice = document.getElementById("currentPrice");
    const auctionCurrentPrice = document.getElementById("auctionCurrentPrice");
    const bidHistory = document.getElementById("bidHistory");

    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    connectWebSocket();

    if (bidForm) {
        bidForm.addEventListener("submit", function (event) {
            event.preventDefault();

            fetch("/api/lots/" + lotId + "/bids", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({
                    amount: bidAmountInput.value
                })
            })
                .then(async function (response) {
                    const data = await response.json();

                    if (!response.ok) {
                        throw new Error(data.message || "Ошибка при отправке ставки");
                    }

                    showMessage(data.message, "success");
                    bidAmountInput.value = "";
                })
                .catch(function (error) {
                    showMessage(error.message, "error");
                });
        });
    }

    function connectWebSocket() {
        const socket = new SockJS("/ws");
        const stompClient = Stomp.over(socket);

        stompClient.debug = null;

        stompClient.connect({}, function () {
            stompClient.subscribe("/topic/lots/" + lotId, function (message) {
                const data = JSON.parse(message.body);

                currentPrice.textContent = data.currentPrice;
                auctionCurrentPrice.textContent = data.currentPrice;

                addBidToHistory(data);
            });
        });
    }

    function addBidToHistory(data) {
        const emptyText = bidHistory.querySelector("p");

        if (emptyText && emptyText.textContent.includes("Ставок пока нет")) {
            emptyText.remove();
        }

        const card = document.createElement("div");
        card.className = "bid-card";

        const firstLine = document.createElement("p");
        firstLine.innerHTML = "<b>" + escapeHtml(data.userName) + "</b> сделал ставку <b>"
            + escapeHtml(data.amount) + "</b> кредитов";

        const secondLine = document.createElement("p");
        secondLine.textContent = data.createdAt;

        card.appendChild(firstLine);
        card.appendChild(secondLine);

        bidHistory.prepend(card);
    }

    function showMessage(text, type) {
        bidMessage.textContent = text;
        bidMessage.className = type === "success" ? "success" : "error";
    }

    function escapeHtml(text) {
        return String(text)
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }
});