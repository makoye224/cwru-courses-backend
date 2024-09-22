const express = require('express');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');

require('dotenv').config();

const courseRoutes = require('./routes/courseRoutes');
const reviewRoutes = require('./routes/reviewRoutes');

const MONGO_URL = process.env.MONGO_URL

// Initialize express app
const app = express();

// Middleware
app.use(bodyParser.json());

// Connect to MongoDB
mongoose.connect(MONGO_URL, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('MongoDB connected'))
  .catch(err => console.error(err));


// Routes
app.use('/api/courses', courseRoutes);
app.use('/api/reviews', reviewRoutes);
const authRoutes = require('./routes/authRoutes');
app.use('/api/courses', courseRoutes);
app.use('/api/authenticate', authRoutes);

// Start the server
const PORT = process.env.PORT || 5001;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
