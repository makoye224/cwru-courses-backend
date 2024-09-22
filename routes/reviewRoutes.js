const express = require('express');
const router = express.Router();
const reviewController = require('../controllers/reviewController');

// Route to create a new review
router.post('/', reviewController.createReview);

// Route to delete a review
router.delete('/:id', reviewController.deleteReview);

// Route to update a review
router.put('/:id', reviewController.updateReview);

module.exports = router;
