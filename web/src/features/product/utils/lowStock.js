export const DEFAULT_LOW_STOCK_THRESHOLD = 5;

export const isLowStock = (product) =>
  product.quantity < (product.lowStockThreshold ?? DEFAULT_LOW_STOCK_THRESHOLD);