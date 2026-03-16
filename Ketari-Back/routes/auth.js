import express from 'express';
import Auth from '../models/auth.js';

const auth = express.Router();

auth.post("/register",async(req,res)=>{
    try {
        const existingEmail = await Auth.findOne({email: req.body.email});
        if (existingEmail) {
            return res.status(400).json({error:"Email already exists"})
        }
        const newUser = new Auth({
            firstName: req.body.firstName,
            lastName: req.body.lastName,
            email: req.body.email
        });
        await newUser.save();
        res.status(200).json({message:'User Registered'})
    } catch (error) {
        res.status(500).json({error:'Internal Server Error'})
    }
});

export default auth