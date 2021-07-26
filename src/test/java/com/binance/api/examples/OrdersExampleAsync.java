package com.binance.api.examples;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.request.AllOrdersRequest;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

import static com.binance.api.client.domain.account.NewOrder.limitBuy;
import static com.binance.api.client.domain.account.NewOrder.marketBuy;

/**
 * Examples on how to place orders, cancel them, and query account information.
 */
public class OrdersExampleAsync {

  public static void main(String[] args) {
//    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
//      "VEKyJ6vJbSnfxLZC4EopzrD0y8l0gd8RTzn5fbDuwpbr1WYRdBxXH214AGUA6ELU",
//      "I98kulkDvASVAEZKXgrRLzyIfC1hOTnFi8PKVUMb3HwsmURMnntXUDqR4V93z1z0");
//    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(//testnet
//      "DpeZpqh5fem5AZoU0ECLydr7QEncmQUD8a1uSMU0aAmzARxlR6WNVpuGVaaw9ZHX",
//      "6T52OaxeQgnZvvXyioXVQagI3UjzUNRe5zCV8d4cXgRT2qOVjU7hTnLWKD7ufzsK");
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(//testnet
      "20197903cd43507e75532d6a6127d517b77df736c4550579bbe51ded1d8c316b",
      "9ab96777c8e2096fc1c360f51e856116e7ae487763ee499afcdcd106f95195b3");



    BinanceApiAsyncRestClient client = factory.newAsyncRestClient();

    // Getting list of open orders
//    client.getOpenOrders(new OrderRequest("BTCUSDT"), response -> System.out.println(response));
//
//    // Get status of a particular order
//    client.getOrderStatus(new OrderStatusRequest("BTCUSDT", 745262L),
//        response -> System.out.println(response));
//
//    // Getting list of all orders with a limit of 10
//    client.getAllOrders(new AllOrdersRequest("BTCUSDT").limit(10), response -> System.out.println(response));
//
//    // Canceling an order
//    client.cancelOrder(new CancelOrderRequest("BTCUSDT", 756703L),
//        response -> System.out.println(response));

    // Placing a test LIMIT order
//    client.newOrderTest(limitBuy("BTCUSDT", TimeInForce.GTC, "1000", "0.0001"),
//        response -> System.out.println("Test order has succeeded."));
//
//    // Placing a test MARKET order
//    client.newOrderTest(marketBuy("BTCUSDT", "1000"), response -> System.out.println("Test order has succeeded."));

    // Placing a real LIMIT order
    client.newOrder(limitBuy("BTCUSDT", TimeInForce.GTC, "1", "0.0001"),
        response -> System.out.println(response));
  }
}
