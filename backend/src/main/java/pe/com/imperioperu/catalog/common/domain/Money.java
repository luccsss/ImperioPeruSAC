package pe.com.imperioperu.catalog.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public class Money {
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 17, fraction = 2)
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3)
    private CurrencyCode currency;

    protected Money() {}

    public Money(BigDecimal amount, CurrencyCode currency) {
        this.amount = Objects.requireNonNull(amount, "amount").setScale(2, RoundingMode.UNNECESSARY);
        if (this.amount.signum() < 0) throw new IllegalArgumentException("Money amount cannot be negative");
        this.currency = Objects.requireNonNull(currency, "currency");
    }

    public static Money usd(BigDecimal amount) { return new Money(amount, CurrencyCode.USD); }
    public BigDecimal getAmount() { return amount; }
    public CurrencyCode getCurrency() { return currency; }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    private void requireSameCurrency(Money other) {
        if (other == null || currency != other.currency) {
            throw new IllegalArgumentException("Money values must use the same currency");
        }
    }

    @Override public boolean equals(Object value) {
        return value instanceof Money other && amount.compareTo(other.amount) == 0 && currency == other.currency;
    }
    @Override public int hashCode() { return Objects.hash(amount.stripTrailingZeros(), currency); }
}

