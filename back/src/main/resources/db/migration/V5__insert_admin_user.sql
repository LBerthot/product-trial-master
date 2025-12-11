INSERT INTO public.users (email, firstname, "password", username)
VALUES (
    'admin@admin.com',
    'test',
    '$2a$10$LEeT/blShUsqUje33NFZg.KF04mAmSs1arP2pirElNa8r20ra0dXK',
    'test'
)
ON CONFLICT (id) DO NOTHING;