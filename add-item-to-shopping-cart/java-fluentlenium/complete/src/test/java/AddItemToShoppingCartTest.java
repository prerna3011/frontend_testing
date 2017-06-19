import org.fluentlenium.adapter.testng.FluentTestNg;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemToShoppingCartTest extends FluentTestNg {

    private static final Logger LOG = Logger.getLogger(AddItemToShoppingCartTest.class.getName());

    @Override
    public String getRemoteUrl() {
        return "http://localhost:4444/wd/hub";
    }

    @Override
    public String getWebDriver() {
        return "remote";
    }

    @Override
    public Capabilities getCapabilities() {
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();
        desiredCapabilities.setCapability(CapabilityType.PLATFORM, Platform.LINUX);
        return desiredCapabilities;
    }

    /*
        Go to Zalando home page, search for "Nike", click on the first product, add it to the basket and
        assert that the product name and value is the correct one.
     */
    @Test
    public void searchProductAndAddItToBag() throws InterruptedException {
        // Go to the homepage
        LOG.info("Loading https://www.zalando.de/...");
        window().maximize();
        goTo("https://www.zalando.de/");

        LOG.info("Type Nike in the search field...");
        find(By.id("searchContent")).write("Nike").submit();

        LOG.info("Click on the first item...");
        find(By.className("catalogArticlesList_item")).first().click();

        LOG.info("Get product brand and name...");
        String expectedProductBrand =
                find(By.cssSelector(".z-text-block.zvui-product-title-brandname.z-text.z-text-body.z-text-black")).text();
        String expectedProductName =
                find(By.cssSelector(".z-text-block.zvui-product-title-productname.z-text.z-text-body.z-text-black")).text();

        LOG.info("Click on the first available size...");
        if (find(By.className("zvui-size-select-dropdown-placeholder")).present()) {
            find(By.className("zvui-size-select-dropdown-placeholder")).first().click();
            find(By.className("zvui-size-select-dropdown-option")).first().click();
        } else {
            find(By.cssSelector(".z-vegas-ui_sizeItem.z-vegas-ui_interactable.z-vegas-ui_sizeList_listItem")).first().click();
        }


        LOG.info("Add product to shopping cart...");
        find(By.cssSelector(".z-richButton.z-richButton-primary")).click();

        LOG.info("Go to shopping cart...");
        // Not possible to get a visible unique element for the shopping cart, and there are currently 5 elements
        // with the same class. The shopping cart is the last one. The test may break when they change the order.
        find(By.cssSelector("div[class='z-navicat-header_userAccNaviItem']")).last().click();

        LOG.info("Assert product brand and name...");
        FluentList<FluentWebElement> productInfo = find(By.className("z-coast-fjord_link"));
        String productBrand = productInfo.get(1).text();
        String productName = productInfo.last().find(By.cssSelector(".z-text.z-text-default")).text();
        assertThat(expectedProductBrand).isEqualToIgnoringCase(productBrand);
        assertThat(expectedProductName).isEqualToIgnoringCase(productName);
    }
}
