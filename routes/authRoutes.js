const express = require('express');
const router = express.Router();


router.post('/', (req, res) => {
  const { userID } = req.body;
  res.status(200).json({ userID });
});

module.exports = router;
