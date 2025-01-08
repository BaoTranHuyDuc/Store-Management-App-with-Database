public class OwnerController {
    private DataAdapter dataAdapter;

    public OwnerController(DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    public boolean orderMoreQuantity(int productId, int additionalQuantity) {
        Product product = dataAdapter.loadProduct(productId);
        if (product == null) {
            return false;
        }
        product.setQuantity(product.getQuantity() + additionalQuantity);
        dataAdapter.saveProduct(product);
        return true; 
    }
}
