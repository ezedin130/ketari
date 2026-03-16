import express from 'express';
import mongoose from 'mongoose';
import auth from './routes/auth.js';

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(auth);

mongoose.connect('mongodb://localhost:27017/ketari')
.then(()=> console.log("Database Connected"))
.catch((err)=> console.error("Error Connectin Database",err))

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
});
app.get("/", (req, res) => {
  res.status(200).json({ message: "Hello" });
}); 