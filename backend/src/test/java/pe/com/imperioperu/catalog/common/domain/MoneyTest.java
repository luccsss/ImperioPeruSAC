package pe.com.imperioperu.catalog.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {
    @Test
    void keepsExactDecimalAndCurrency() {
        var result = Money.usd(new BigDecimal("65.00")).add(Money.usd(new BigDecimal("10.25")));
        assertThat(result.getAmount()).isEqualByComparingTo("75.25");
        assertThat(result.getCurrency()).isEqualTo(CurrencyCode.USD);
    }

    @Test
    void rejectsCurrencyMixing() {
        var usd = Money.usd(new BigDecimal("65.00"));
        var pen = new Money(new BigDecimal("10.00"), CurrencyCode.PEN);
        assertThatThrownBy(() -> usd.add(pen)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMoreThanTwoFractionDigitsInsteadOfRoundingSilently() {
        assertThatThrownBy(() -> Money.usd(new BigDecimal("65.001"))).isInstanceOf(ArithmeticException.class);
    }
}

