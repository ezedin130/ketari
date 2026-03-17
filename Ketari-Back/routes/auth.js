import express from 'express';
import Auth from '../models/auth.js';
import jwt from "jsonwebtoken";


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

auth.post("/login",async (req,res)=>{
    try {
        const user = await Auth.findOne({email:req.body.email})
        if (!user) {
            return res.status(400).json({error:"Invalid Email"})
        }
        const token = jwt.sign({email:user.email},'secret');
        res.status(200).json({token,message:'user Logged In'})
    } catch (error) {
        res.status(500).json({error:'Internal Server Error'})
    }
});

export default auth