const request = require('supertest');
const express = require('express');
const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');
const courseRoutes = require('../routes/courseRoutes');  // Update to correct path

const app = express();
app.use(express.json());
app.use('/api/courses', courseRoutes);

let mongoServer;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  const uri = mongoServer.getUri();
  await mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true });
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

describe('GET /api/courses', () => {
  it('should return a 200 status code', async () => {
    const res = await request(app).get('/api/courses');
    expect(res.statusCode).toEqual(200);
  });
});
