package pe.com.imperioperu.catalog.inventory.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import pe.com.imperioperu.catalog.catalog.domain.Book;
import pe.com.imperioperu.catalog.catalog.domain.BookOffer;
import pe.com.imperioperu.catalog.common.domain.Money;
import pe.com.imperioperu.catalog.organization.domain.Organization;
import pe.com.imperioperu.catalog.organization.domain.OrganizationRole;

class InventoryItemTest {
    @Test
    void reservationsAffectAvailableStockAndKeepHistoryReadyState() {
        var item = item(10, 2);
        item.reserve(3);
        assertThat(item.getOnHand()).isEqualTo(10);
        assertThat(item.getReserved()).isEqualTo(3);
        assertThat(item.available()).isEqualTo(7);
        assertThat(item.status()).isEqualTo(InventoryStatus.IN_STOCK);
        item.release(2);
        assertThat(item.available()).isEqualTo(9);
    }

    @Test
    void cannotReserveOrAdjustBelowAvailableStock() {
        var item = item(2, 1);
        assertThatThrownBy(() -> item.reserve(3)).isInstanceOf(IllegalArgumentException.class);
        item.reserve(2);
        assertThatThrownBy(() -> item.adjustOnHand(-1)).isInstanceOf(IllegalArgumentException.class);
    }

    private InventoryItem item(int onHand, int minimum) {
        var book = new Book("Fixture de prueba");
        var seller = new Organization("Seller Test", "Seller", null, EnumSet.of(OrganizationRole.SELLER));
        var offer = new BookOffer(book, seller, "SKU-TEST", "fixture-prueba", Money.usd(new BigDecimal("10.00")));
        return new InventoryItem(offer, onHand, minimum);
    }
}

