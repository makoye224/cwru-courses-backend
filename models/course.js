const mongoose = require('mongoose');

const reviewSchema = new mongoose.Schema({
  createdBy: { type: String, required: true },
  overall: { type: Number, required: true },
  difficulty: { type: Number, required: true },
  usefulness: { type: Number, required: true },
  major: { type: String, required: true },
  anonymous: { type: Boolean, default: false },
  additionalComments: String,
  tips: String,
  createdAt: { type: Date, default: Date.now },
  professor: String
});

const courseSchema = new mongoose.Schema({
  title: { type: String, required: true },
  createdBy: { type: String, required: true },
  createdAt: { type: Date, default: Date.now },
  description: String,
  aliases: [String],
  prerequisites: [String],
  reviews: [reviewSchema]
});

module.exports = mongoose.model('Course', courseSchema);
