import mongoose from "mongoose";
const AuthSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  firstName: {
    type: String,
    required: true,
    trim: true,
  },
  lastName:{
    type: String,
    required: true,
    trim: true,
  }
},
  {
    timestamps: true,
  }); 

export default mongoose.model('Auth',AuthSchema);
