const express = require('express');
const router = express.Router();
const courseController = require('../controllers/courseController');

// Route to create a new course
router.post('/', courseController.createCourse);

// Route to search for a course
router.get('/search', courseController.searchCourse);

// Route to get all courses
router.get('/', courseController.getAllCourses);

// Route to get a course by ID
router.get('/:id', courseController.getCourseById);

module.exports = router;
