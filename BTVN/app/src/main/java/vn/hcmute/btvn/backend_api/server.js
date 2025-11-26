const express = require('express');
const app = express();
const port = 3000;

app.use(express.json());
//db
const categories = [
  { id: 1, name: 'Điện tử' },
  { id: 2, name: 'Sách' },
  { id: 3, name: 'Quần áo' },
];

const products = [
  { id: 101, categoryId: 1, name: 'Laptop ABC', sales: 150, createdAt: new Date('2024-05-20') },
  { id: 102, categoryId: 1, name: 'Chuột không dây', sales: 300, createdAt: new Date() },
  { id: 201, categoryId: 2, name: 'Sách "Lập trình Node.js"', sales: 500, createdAt: new Date('2024-03-15') },
  { id: 301, categoryId: 3, name: 'Áo thun', sales: 50, createdAt: new Date(new Date().setDate(new Date().getDate() - 2)) },
  { id: 103, categoryId: 1, name: 'Bàn phím cơ', sales: 250, createdAt: new Date(new Date().setDate(new Date().getDate() - 5)) },
  { id: 202, categoryId: 2, name: 'Sách "Clean Code"', sales: 480, createdAt: new Date(new Date().setDate(new Date().getDate() - 1)) },
];

// === APIs ===

// API lấy tất cả danh mục
app.get('/api/categories', (req, res) => {
  res.json(categories);
});

// API lấy tất cả sản phẩm
app.get('/api/products', (req, res) => {
    res.json(products);
});

// API lấy sản phẩm theo danh mục cụ thể
app.get('/api/categories/:categoryId/products', (req, res) => {
  const categoryId = parseInt(req.params.categoryId, 10);
  const productsInCategory = products.filter(p => p.categoryId === categoryId);
  res.json(productsInCategory);
});

// API lấy sản phẩm bán chạy (có thể lọc theo danh mục)
app.get('/api/products/top-sellers', (req, res) => {
  let productPool = [...products];

  // Nếu có categoryId trong query, thì lọc trước
  if (req.query.categoryId) {
    const categoryId = parseInt(req.query.categoryId, 10);
    if (!isNaN(categoryId)) {
      productPool = products.filter(p => p.categoryId === categoryId);
    }
  }

  const topProducts = productPool
    .sort((a, b) => b.sales - a.sales)
    .slice(0, 10);
  res.json(topProducts);
});

// API lấy sản phẩm mới nhất (có thể lọc theo danh mục)
app.get('/api/products/newest', (req, res) => {
  const sevenDaysAgo = new Date();
  sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

  let productPool = [...products];

  // Nếu có categoryId trong query, thì lọc trước
  if (req.query.categoryId) {
    const categoryId = parseInt(req.query.categoryId, 10);
    if (!isNaN(categoryId)) {
      productPool = products.filter(p => p.categoryId === categoryId);
    }
  }

  const newProducts = productPool
    .filter(p => p.createdAt >= sevenDaysAgo)
    .sort((a, b) => b.createdAt - a.createdAt)
    .slice(0, 10);
  res.json(newProducts);
});

app.listen(port, () => {
  console.log(`Server Node.js đang chạy tại http://localhost:${port}`);
});
