#version 150

out vec4 outColor;

in vec3 colorPS;
in vec2 texCoordPS;
in vec3 posPS;

uniform sampler2D tex;

void main() 
{
	if(texture(tex, texCoordPS) != 0.0)
	{
		outColor = vec4(posPS, 1.0);//texture(tex, texCoordPS);// * vec4(colorPS, 1.0);
	}
	else
	{
		outColor = vec4(0.0, 0.5, 0.0, 1.0);
	}
}

