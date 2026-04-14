import { useEffect, useState } from "react";
import {
  fetchMe,
  fetchMyBio,
  fetchMyProfile,
  updateMyBio,
  updateMyProfile,
  uploadProfilePicture,
  deleteProfilePicture,
} from "../api/client";

type ProfileForm = {
  displayName: string;
  aboutMe: string;
  location: string;
  preferredDistanceKm: string;
};

type BioForm = {
  hobbies: string;
  musicPreferences: string;
  foodPreferences: string;
  interests: string;
  lookingFor: string;
};

const CITY_OPTIONS = [
  "Tallinn",
  "Tartu",
  "Riga",
  "Helsinki",
  "Vilnius",
  "Oslo",
  "Tokyo",
] as const;

export default function ProfileSetup() {
  const apiBase = import.meta.env.VITE_API_URL ?? "http://localhost:8080";
  const [profile, setProfile] = useState<ProfileForm>({
    displayName: "",
    aboutMe: "",
    location: "",
    preferredDistanceKm: "",
  });
  const [bio, setBio] = useState<BioForm>({
    hobbies: "",
    musicPreferences: "",
    foodPreferences: "",
    interests: "",
    lookingFor: "",
  });
  const [status, setStatus] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [email, setEmail] = useState("");
  const [profilePictureUrl, setProfilePictureUrl] = useState<string | null>(
    null
  );

  useEffect(() => {
    let isActive = true;

    async function load() {
      try {
        // Load profile and bio to prefill the form.
        const me = await fetchMe();
        const existingProfile = await fetchMyProfile();
        const existingBio = await fetchMyBio();

        if (isActive) {
          setProfile({
            displayName: existingProfile.displayName ?? me.name ?? "",
            aboutMe: existingProfile.aboutMe ?? "",
            location: existingProfile.location ?? "",
            preferredDistanceKm:
              existingProfile.preferredDistanceKm != null
                ? String(existingProfile.preferredDistanceKm)
                : "",
          });
          setEmail(me.email ?? existingProfile.email ?? localStorage.getItem("auth_email") ?? "");
          setProfilePictureUrl(existingProfile.profilePictureUrl ?? null);
          setBio({
            hobbies: existingBio.hobbies ?? "",
            musicPreferences: existingBio.musicPreferences ?? "",
            foodPreferences: existingBio.foodPreferences ?? "",
            interests: existingBio.interests ?? "",
            lookingFor: existingBio.lookingFor ?? "",
          });
        }
      } catch (err) {
        if (isActive) {
          setError(err instanceof Error ? err.message : "Failed to load profile");
        }
      }
    }

    load();

    return () => {
      isActive = false;
    };
  }, []);

  function handleProfileChange(field: keyof ProfileForm, value: string) {
    setProfile((prev) => ({ ...prev, [field]: value }));
  }

  function handleBioChange(field: keyof BioForm, value: string) {
    setBio((prev) => ({ ...prev, [field]: value }));
  }

  async function handleSave() {
    setStatus(null);
    setError(null);
    setFieldErrors({});

    const nextFieldErrors: Record<string, string> = {};

    if (!profile.displayName.trim()) {
      nextFieldErrors.displayName = "Display name is required.";
    }
    if (!profile.aboutMe.trim()) {
      nextFieldErrors.aboutMe = "Please add a short bio.";
    }
    if (!profile.location.trim()) {
      nextFieldErrors.location = "Location is required.";
    }
    if (!profile.preferredDistanceKm.trim()) {
      nextFieldErrors.preferredDistanceKm =
        "Preferred distance is required.";
    }
    if (!bio.hobbies.trim()) {
      nextFieldErrors.hobbies = "Add at least one hobby.";
    }
    if (!bio.musicPreferences.trim()) {
      nextFieldErrors.musicPreferences = "Add your music preferences.";
    }
    if (!bio.foodPreferences.trim()) {
      nextFieldErrors.foodPreferences = "Add your food preferences.";
    }
    if (!bio.interests.trim()) {
      nextFieldErrors.interests = "Add at least one interest.";
    }
    if (!bio.lookingFor.trim()) {
      nextFieldErrors.lookingFor = "Tell others what you are looking for.";
    }

    if (Object.keys(nextFieldErrors).length > 0) {
      setFieldErrors(nextFieldErrors);
      setError("Please complete the required fields.");
      return;
    }

    try {
      // Save profile first.
      await updateMyProfile({
        displayName: profile.displayName,
        aboutMe: profile.aboutMe,
        profilePictureUrl,
        location: profile.location,
        preferredDistanceKm: profile.preferredDistanceKm
          ? Number(profile.preferredDistanceKm)
          : null,
      });

      // Save bio.
      await updateMyBio(bio);

      setStatus("Profile saved");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Save failed");
    }
  }

  async function handleUpload(file: File | null) {
    if (!file) return;
    setError(null);
    setStatus(null);

    try {
      const updated = await uploadProfilePicture(file);
      setProfilePictureUrl(updated.profilePictureUrl ?? null);
      setStatus("Profile picture updated");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Upload failed");
    }
  }

  async function handleRemovePhoto() {
    setError(null);
    setStatus(null);

    try {
      const updated = await deleteProfilePicture();
      setProfilePictureUrl(updated.profilePictureUrl ?? null);
      setStatus("Profile picture removed");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Remove failed");
    }
  }

  return (
    <section className="page">
      <header className="page-header">
        <div>
          <p className="eyebrow">Profile Setup</p>
          <h1>Tell people who you are</h1>
          <p className="subtitle">
            Fill out your profile and bio to unlock better recommendations.
          </p>
        </div>
        <button className="primary-button" type="button" onClick={handleSave}>
          Save profile
        </button>
      </header>

      {status && <p className="muted">{status}</p>}
      {error && <p className="muted">{error}</p>}

      <div className="form-grid">
        <div className="form-card">
          <h2>Profile</h2>
          <div className="photo-row">
            {/*
              If backend returns a relative URL (e.g. /uploads/xyz.jpg),
              prefix it with the API base so the browser can load it.
            */}
            {(() => {
              const displayUrl = profilePictureUrl?.startsWith("/")
                ? `${apiBase}${profilePictureUrl}`
                : profilePictureUrl;
              return (
                <div className="photo-preview">
                  {displayUrl ? (
                    <img src={displayUrl} alt="Profile" />
                  ) : (
                    <span>👤</span>
                  )}
                </div>
              );
            })()}
            <div className="photo-actions">
              <label className="ghost-button">
                Upload photo
                <input
                  type="file"
                  accept="image/*"
                  onChange={(event) => handleUpload(event.target.files?.[0] ?? null)}
                  hidden
                />
              </label>
              <button
                className="ghost-button"
                type="button"
                onClick={handleRemovePhoto}
              >
                Remove
              </button>
            </div>
          </div>
          <label className="form-field">
            <span>Email</span>
            <input type="email" value={email} readOnly />
          </label>
          <label className="form-field">
            <span>Display name</span>
            <input
              type="text"
              value={profile.displayName}
              onChange={(event) =>
                handleProfileChange("displayName", event.target.value)
              }
            />
            {fieldErrors.displayName && (
              <span className="field-error">{fieldErrors.displayName}</span>
            )}
          </label>
          <label className="form-field">
            <span>About me</span>
            <textarea
              value={profile.aboutMe}
              onChange={(event) =>
                handleProfileChange("aboutMe", event.target.value)
              }
            />
            {fieldErrors.aboutMe && (
              <span className="field-error">{fieldErrors.aboutMe}</span>
            )}
          </label>
          <label className="form-field">
            <span>Location</span>
            <select
              value={profile.location}
              onChange={(event) =>
                handleProfileChange("location", event.target.value)
              }
            >
              <option value="" disabled>
                Select your city
              </option>
              {CITY_OPTIONS.map((city) => (
                <option key={city} value={city}>
                  {city}
                </option>
              ))}
            </select>
            {fieldErrors.location && (
              <span className="field-error">{fieldErrors.location}</span>
            )}
          </label>
          <label className="form-field">
            <span>Preferred distance (km)</span>
            <input
              type="number"
              min="1"
              value={profile.preferredDistanceKm}
              onChange={(event) =>
                handleProfileChange("preferredDistanceKm", event.target.value)
              }
            />
            {fieldErrors.preferredDistanceKm && (
              <span className="field-error">
                {fieldErrors.preferredDistanceKm}
              </span>
            )}
          </label>
        </div>

        <div className="form-card">
          <h2>Bio</h2>
          <label className="form-field">
            <span>Hobbies</span>
            <input
              type="text"
              value={bio.hobbies}
              onChange={(event) => handleBioChange("hobbies", event.target.value)}
            />
            {fieldErrors.hobbies && (
              <span className="field-error">{fieldErrors.hobbies}</span>
            )}
          </label>
          <label className="form-field">
            <span>Music preferences</span>
            <input
              type="text"
              value={bio.musicPreferences}
              onChange={(event) =>
                handleBioChange("musicPreferences", event.target.value)
              }
            />
            {fieldErrors.musicPreferences && (
              <span className="field-error">{fieldErrors.musicPreferences}</span>
            )}
          </label>
          <label className="form-field">
            <span>Food preferences</span>
            <input
              type="text"
              value={bio.foodPreferences}
              onChange={(event) =>
                handleBioChange("foodPreferences", event.target.value)
              }
            />
            {fieldErrors.foodPreferences && (
              <span className="field-error">{fieldErrors.foodPreferences}</span>
            )}
          </label>
          <label className="form-field">
            <span>Interests</span>
            <input
              type="text"
              value={bio.interests}
              onChange={(event) =>
                handleBioChange("interests", event.target.value)
              }
            />
            {fieldErrors.interests && (
              <span className="field-error">{fieldErrors.interests}</span>
            )}
          </label>
          <label className="form-field">
            <span>Looking for</span>
            <input
              type="text"
              value={bio.lookingFor}
              onChange={(event) =>
                handleBioChange("lookingFor", event.target.value)
              }
            />
            {fieldErrors.lookingFor && (
              <span className="field-error">{fieldErrors.lookingFor}</span>
            )}
          </label>
        </div>
      </div>
    </section>
  );
}
