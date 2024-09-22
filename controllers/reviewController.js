const Course = require('../models/course');

// Create a new review for a course
exports.createReview = async (req, res) => {
  const { createdBy, overall, difficulty, usefulness, major, anonymous, additionalComments, tips, professor, courseId } = req.body;
  try {
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({ error: 'Course not found' });
    }
    const newReview = { createdBy, overall, difficulty, usefulness, major, anonymous, additionalComments, tips, professor };
    course.reviews.push(newReview);
    await course.save();
    res.status(201).json({ ID: newReview._id });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Delete a review
exports.deleteReview = async (req, res) => {
  const { id } = req.params;
  const { courseId } = req.body;
  try {
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({ error: 'Course not found' });
    }
    course.reviews.id(id).remove();
    await course.save();
    res.status(200).json({ message: 'Review deleted' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

// Update a review
exports.updateReview = async (req, res) => {
  const { id } = req.params;
  const { courseId, overall, difficulty, usefulness, major, anonymous, additionalComments, tips, professor } = req.body;
  try {
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({ error: 'Course not found' });
    }
    const review = course.reviews.id(id);
    if (!review) {
      return res.status(404).json({ error: 'Review not found' });
    }
    review.overall = overall || review.overall;
    review.difficulty = difficulty || review.difficulty;
    review.usefulness = usefulness || review.usefulness;
    review.major = major || review.major;
    review.anonymous = anonymous !== undefined ? anonymous : review.anonymous;
    review.additionalComments = additionalComments || review.additionalComments;
    review.tips = tips || review.tips;
    review.professor = professor || review.professor;
    await course.save();
    res.status(200).json({ message: 'Review updated' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};
