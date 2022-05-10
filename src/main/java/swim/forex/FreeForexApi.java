// Copyright 2015-2022 SWIM.AI inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package swim.forex;

import swim.api.agent.AgentContext;
import swim.structure.Item;
import swim.structure.Value;

public final class FreeForexApi {

  private FreeForexApi() {
  }

  private static final String STANDARD = "USD";
  private static final String[] NON_STD_CURRENCIES = {"EUR", "JPY", "GBP", "CHF",
      "CAD", "AUD", "NZD", "ZAR", "INR"};
  private static final String[] CURRENCY_PAIRS = new String[NON_STD_CURRENCIES.length];

  static {
    for (int i = 0; i < CURRENCY_PAIRS.length; i++) {
      CURRENCY_PAIRS[i] = STANDARD + NON_STD_CURRENCIES[i];
    }
  }

  private static final String FREE_FOREX_API_URI = "https://www.freeforexapi.com/api/live"
      + "?pairs=" + String.join(",", CURRENCY_PAIRS);

  public static String freeForexApiUri() {
    return FREE_FOREX_API_URI;
  }

  public static void relayExchangeRates(AgentContext swim, Value response) {
    if (response.get("code").intValue(0) / 100 != 2) {
      swim.warn("Non-successful response code: " + response);
      return;
    }
    // Partition this payload by currencies. Each partition will look like:
    //   {"USDEUR":{"rate":0.91943,"timestamp":1646639942}}
    final Value rates = response.get("rates");
    if (!rates.isDistinct() || rates.length() == 0) {
      swim.warn("Unexpected payload; possible data error or API change: " + response);
      return;
    }
    for (Item i : rates) {
      // Identify the "destination" currency, which maps to a Web Agent.
      // The above input will yield "EUR".
      final String currency = i.key().stringValue().substring(3);
      // Identify the payload we wish to send to that Web Agent. The above
      // input will yield "{"rate":0.91943,"timestamp":1646639942}".
      final Value agentPayload = i.toValue();
      // Send the message via the Swim API.
      swim.command("/currency/" + currency, "addEvent", agentPayload);
    }
  }

}
