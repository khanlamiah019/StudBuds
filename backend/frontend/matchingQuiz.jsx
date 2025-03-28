import React, { useState } from "react";

const Card = ({ children, className }) => (
  <div className={`p-6 bg-mint-500 shadow-lg rounded-2xl ${className}`}>{children}</div>
);

const CardContent = ({ children }) => <div className="p-4">{children}</div>;

const Button = ({ children, onClick, variant }) => (
  <button
    onClick={onClick}
    className={`px-4 py-2 rounded-lg text-white transition-all font-semibold ${
      variant === "destructive" ? "bg-red-600 hover:bg-red-700" : "bg-green-600 hover:bg-green-700"
    }`}
  >
    {children}
  </button>
);

const Input = ({ name, value, onChange, type = "text" }) => (
  <input
    name={name}
    value={value}
    onChange={onChange}
    type={type}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400"
  />
);

const Label = ({ children }) => <label className="block font-semibold mb-1 text-green-800">{children}</label>;

const Textarea = ({ name, value, onChange }) => (
  <textarea
    name={name}
    value={value}
    onChange={onChange}
    className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400"
  />
);

const ProfilePage = () => {
  const [profile, setProfile] = useState({
    name: "",
    email: "",
    major: "",
    strengths: "",
    weaknesses: "",
    availableTimes: "",
    gradeLevel: "1st Year"
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile((prev) => ({ ...prev, [name]: value }));
  };

  const handleDeleteAccount = () => {
    if (window.confirm("Are you sure you want to delete your account? This action is irreversible.")) {
      console.log("Account deleted"); // Implement deletion logic
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form submitted:", profile);
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-mint-200 p-4">
      <Card className="w-full max-w-md bg-mint-300 shadow-lg p-6 rounded-2xl">
        <CardContent>
          <h2 className="text-xl font-bold mb-4 text-green-900">Quiz Form</h2>
          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <Label>Name</Label>
              <Input name="name" value={profile.name} onChange={handleChange} />
            </div>
            <div>
              <Label>Email</Label>
              <Input name="email" type="email" value={profile.email} onChange={handleChange} />
            </div>
            <div>
              <Label>Strengths</Label>
              <Textarea name="strengths" value={profile.strengths} onChange={handleChange} />
            </div>
            <div>
              <Label>Weaknesses</Label>
              <Textarea name="weaknesses" value={profile.weaknesses} onChange={handleChange} />
            </div>
            <div>
              <Label>Available Times</Label>
              <Textarea name="availableTimes" value={profile.availableTimes} onChange={handleChange} />
            </div>
            <div>
              <Label>Grade Level</Label>
              <select
                name="gradeLevel"
                value={profile.gradeLevel}
                onChange={handleChange}
                className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-400"
              >
                <option>1st Year</option>
                <option>2nd Year</option>
                <option>3rd Year</option>
                <option>4th Year</option>
                <option>5th Year</option>
              </select>
            </div>
            <div className="flex justify-between mt-4">
              <Button type="submit">Submit</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export default ProfilePage;
