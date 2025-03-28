import React, { useState } from "react";

const Card = ({ children, className }) => (
  <div className={`p-4 bg-white shadow-md rounded-lg ${className}`}>{children}</div>
);

const CardContent = ({ children }) => <div className="p-4">{children}</div>;

const Button = ({ children, onClick, variant }) => (
  <button
    onClick={onClick}
    className={`px-4 py-2 rounded-lg text-white ${
      variant === "destructive" ? "bg-red-600 hover:bg-red-700" : "bg-blue-600 hover:bg-blue-700"
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
    className="w-full p-2 border rounded-lg"
  />
);

const Label = ({ children }) => <label className="block font-semibold mb-1">{children}</label>;

const Textarea = ({ name, value, onChange }) => (
  <textarea
    name={name}
    value={value}
    onChange={onChange}
    className="w-full p-2 border rounded-lg"
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

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 p-4">
      <Card className="w-full max-w-md bg-white shadow-md p-6 rounded-2xl">
        <CardContent>
          <h2 className="text-xl font-bold mb-4">Profile</h2>
          <div className="space-y-3">
            <div>
              <Label>Name</Label>
              <Input name="name" value={profile.name} onChange={handleChange} />
            </div>
            <div>
              <Label>Email</Label>
              <Input name="email" type="email" value={profile.email} onChange={handleChange} />
            </div>
            <div>
              <Label>Major</Label>
              <Input name="major" value={profile.major} onChange={handleChange} />
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
          </div>
          <div className="flex justify-between mt-4">
            <Button variant="destructive" onClick={handleDeleteAccount}>
              Delete Account
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default ProfilePage;
