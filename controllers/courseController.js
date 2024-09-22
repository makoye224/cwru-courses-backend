const Course = require('../models/course');

// Create a new course
exports.createCourse = async (req, res) => {
  const { title, createdBy, description, aliases, prerequisites } = req.body;
  try {
    const newCourse = new Course({ title, createdBy, description, aliases, prerequisites });
    const savedCourse = await newCourse.save();
    res.status(201).json({ ID: savedCourse._id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Search for a course
exports.searchCourse = async (req, res) => {
  const { text } = req.body;
  try {
    const courses = await Course.find({ $text: { $search: text } });
    res.status(200).json({ courses });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Get all courses
exports.getAllCourses = async (req, res) => {
  try {
    const courses = await Course.find();
    res.status(200).json({ courses });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Get a course by ID
exports.getCourseById = async (req, res) => {
  const { id } = req.params;
  try {
    const course = await Course.findById(id);
    if (!course) {
      return res.status(404).json({ error: 'Course not found' });
    }
    res.status(200).json({ course });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
